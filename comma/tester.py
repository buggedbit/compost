import argparse
import sys
from preprocessor import generate_tokenizer_on_all_essays, encode_essay_data_for_testing
from qwk import quadratic_weighted_kappa
import numpy as np
from keras.models import model_from_json
from layers import Conv1DWithMasking, MeanOverTime

# arguments
parser = argparse.ArgumentParser()

# required args
parser.add_argument('--MODEL_FILE', required=True)
parser.add_argument('--MODEL_WEIGHT_FILES', required=True, nargs='+')
parser.add_argument('--TEST_DATA_FILE', required=True)
parser.add_argument('--OUTPUT_FILE_PREFIX', required=True)
parser.add_argument('--OVERALL_SCORE_COLUMN', required=True, type=int)
parser.add_argument('--ATTR_SCORE_COLUMNS', required=True, type=int, nargs='+')
parser.add_argument('--OVERALL_MIN_SCORE', required=True, type=int)
parser.add_argument('--OVERALL_MAX_SCORE', required=True, type=int)
parser.add_argument('--ATTR_MIN_SCORE', required=True, type=int)
parser.add_argument('--ATTR_MAX_SCORE', required=True, type=int)
# optional args
parser.add_argument('--MAX_ESSAY_LENGTH', type=int, default=2000)
parser.add_argument('--VOCAB_FILE', default='data/vocab_db.txt')
args = parser.parse_args()

# log arguments
print(args)

# pre processing
# tokenizer prep
tokenizer_files = [args.VOCAB_FILE, ]
for propmt in range(1, 9):
    for fold in range(5):
        tokenizer_files.append('data/prompts-and-folds/Prompt-{}-Train-{}.csv'.format(propmt, fold))
        tokenizer_files.append('data/prompts-and-folds/Prompt-{}-Test-{}.csv'.format(propmt, fold))

tokenizer = generate_tokenizer_on_all_essays(tuple(tokenizer_files))
vocab_size = len(tokenizer.word_index) + 1
essay_ids, essays, true_scores = encode_essay_data_for_testing(args.TEST_DATA_FILE, args.OVERALL_SCORE_COLUMN, args.ATTR_SCORE_COLUMNS, args.MAX_ESSAY_LENGTH, tokenizer)

def predict_and_store(model, essays, true_scores, overall_min_score, overall_max_score, attr_min_score, attr_max_score, output_file, attr_number):
    pred_norm_score_tensor = model.predict(essays, verbose=0)

    qwks = []
    pred_true_scores_tensor = []
    # overall score
    pred_norm_scores = pred_norm_score_tensor[0]
    pred_norm_scores = np.reshape(pred_norm_scores, pred_norm_scores.shape[0])
    pred_true_scores = np.round(overall_min_score + pred_norm_scores * (overall_max_score - overall_min_score))
    pred_true_scores_tensor.append(pred_true_scores)
    qwk = quadratic_weighted_kappa(pred_true_scores, true_scores[0], min_rating=overall_min_score, max_rating=overall_max_score)
    qwks.append(qwk)
    # attribute scores
    for i in range(1, len(pred_norm_score_tensor)):
        pred_norm_scores = pred_norm_score_tensor[i]
        pred_norm_scores = np.reshape(pred_norm_scores, pred_norm_scores.shape[0])
        pred_true_scores = np.round(attr_min_score + pred_norm_scores * (attr_max_score - attr_min_score))
        pred_true_scores_tensor.append(pred_true_scores)
        qwk = quadratic_weighted_kappa(pred_true_scores, true_scores[i], min_rating=attr_min_score, max_rating=attr_max_score)
        qwks.append(qwk)

    print('model-for-attr-{}: qwks:{}'.format(attr_number, qwks))
    sys.stdout.flush()

    with open('{}A{}'.format(output_file, attr_number), 'a+') as file:
        for i, essay_id in enumerate(essay_ids):
            file.write('{}\t{}\t{}\n'.format(essay_id, int(true_scores[attr_number][i]), int(pred_true_scores_tensor[attr_number][i])))
    file.close()

model = None
with open(args.MODEL_FILE, 'r') as json_file:
    model = model_from_json(json_file.read(), custom_objects={'Conv1DWithMasking': Conv1DWithMasking, 'MeanOverTime': MeanOverTime})

for i, weight_file in enumerate(args.MODEL_WEIGHT_FILES):
    # load the model
    model.load_weights(weight_file)
    predict_and_store(model, essays, true_scores, args.OVERALL_MIN_SCORE, args.OVERALL_MAX_SCORE, args.ATTR_MIN_SCORE, args.ATTR_MAX_SCORE, args.OUTPUT_FILE_PREFIX, i)


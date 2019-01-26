import argparse
import sys
from preprocessor import generate_tokenizer_on_all_essays, encode_essay_data_for_testing
from qwk import quadratic_weighted_kappa
import numpy as np
from keras.models import model_from_json
from layers import Conv1DWithMasking, MeanOverTime

def get_qwk(model, essays, true_scores, overall_min_score, overall_max_score, attr_min_score, attr_max_score):
    pred_norm_score_tensor = model.predict(essays, verbose=1)
    # clipping in case of linear final activation
    for i, pred_norm_scores in enumerate(pred_norm_score_tensor):
        for j, score in enumerate(pred_norm_scores):
            if score > 1:
                pred_norm_score_tensor[i][j] = 1
            elif score < 0:
                pred_norm_score_tensor[i][j] = 0

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
    return qwks, pred_true_scores_tensor


# arguments
parser = argparse.ArgumentParser()

# required args
parser.add_argument('--MODEL_FILE', required=True)
parser.add_argument('--MODEL_WEIGHTS_FILE', required=True)
parser.add_argument('--TEST_DATA_FILE', required=True)
parser.add_argument('--OUTPUT_FILE', required=True)
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
print('-------- -------- Pre Processing')
tokenizer = generate_tokenizer_on_all_essays((args.VOCAB_FILE,))
essay_ids, essays, true_scores = encode_essay_data_for_testing(args.TEST_DATA_FILE, args.OVERALL_SCORE_COLUMN, args.ATTR_SCORE_COLUMNS, args.MAX_ESSAY_LENGTH, tokenizer)

print('-------- -------- Model loading')
model = None
# load the model
with open(args.MODEL_FILE, 'r') as json_file:
    model = model_from_json(json_file.read(), custom_objects={'Conv1DWithMasking': Conv1DWithMasking, 'MeanOverTime': MeanOverTime})
model.load_weights(args.MODEL_WEIGHTS_FILE)
print(model.summary())

print('-------- -------- Evaluating Model')
qwks, predicted_scores = get_qwk(model, essays, true_scores, args.OVERALL_MIN_SCORE, args.OVERALL_MAX_SCORE, args.ATTR_MIN_SCORE, args.ATTR_MAX_SCORE)

print(qwks)
# open log file
sys.stdout = open('%s' % (args.OUTPUT_FILE), 'w')

print('essay_id,P_overall_score,T_overall_score,', end='')
col_names = []
for i in range(len(true_scores) - 1):
  col_names.append('P_attr%d_score' % i)
  col_names.append('T_attr%d_score' % i)
print(','.join(col_names))

for i, essay_id in enumerate(essay_ids):
  print('%d,' % essay_id, end='')
  scores = []
  for j in range(len(true_scores)):
    scores.append(int(predicted_scores[j][i]))
    scores.append(int(true_scores[j][i]))
  print(','.join(map(str, scores)))

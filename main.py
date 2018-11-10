import argparse
import sys
import os
from model_generator import generate_model
from preprocessor import generate_tokenizer_on_all_essays, encode_essay_data, load_word_embeddings_dict, get_word_embeddings_matrix
from quadratic_weighted_kappa import quadratic_weighted_kappa
import numpy as np
from utils import Stats

def get_qwk(model, essays, true_scores, min_score, max_score):
    pred_norm_score_tensor = model.predict(essays, verbose=0)
    qwks = []
    for i, pred_norm_scores in enumerate(pred_norm_score_tensor):
        pred_norm_scores = np.reshape(pred_norm_scores, pred_norm_scores.shape[0])
        pred_true_score = np.round(min_score + pred_norm_scores * (max_score - min_score))
        qwk = quadratic_weighted_kappa(pred_true_score, true_scores[i], min_rating=min_score, max_rating=max_score)
        qwks.append(qwk)
    return qwks

# arguments
parser = argparse.ArgumentParser()
# required args
parser.add_argument('--NUM_EPOCHS', required=True, type=int)
parser.add_argument('--MIN_SCORE', required=True, type=int)
parser.add_argument('--MAX_SCORE', required=True, type=int)
parser.add_argument('--TRAINING_DATA_FILE', required=True)
parser.add_argument('--VALIDATION_DATA_FILE', required=True)
parser.add_argument('--WORD_EMB_FILE', required=True)
parser.add_argument('--OUTPUT_DIR', required=True)
parser.add_argument('--SCORE_COLUMNS', required=True, type=int, nargs='+')
parser.add_argument('--LOSS_WEIGHTS', required=True, type=float, nargs='+')
# optional args
parser.add_argument('--MAX_ESSAY_LENGTH', type=int, default=2000)
parser.add_argument('--EMBEDDING_SIZE', type=int, default=300)
parser.add_argument('--LOG_FILE', default='log.txt')
parser.add_argument('--VOCAB_FILE', default='data/vocab_db.txt')
parser.add_argument('--MODEL_DEF_FILE', default='model.json')
parser.add_argument('--STATS_GRAPH_FILE', default='training_stats.png')
args = parser.parse_args()

# assert output dir exists
assert (os.path.isdir(args.OUTPUT_DIR))
assert (len(args.SCORE_COLUMNS) == len(args.LOSS_WEIGHTS))

# open log file
sys.stdout = open('%s/%s' % (args.OUTPUT_DIR, args.LOG_FILE), 'w')

# log arguments
print(args)
# write to stdout
sys.stdout.flush()

# pre processing
print('-------- -------- Pre Processing')
tokenizer = generate_tokenizer_on_all_essays((args.VOCAB_FILE,))
vocab_size = len(tokenizer.word_index) + 1

tr_essays, tr_true_scores, tr_norm_scores = encode_essay_data(args.TRAINING_DATA_FILE, args.SCORE_COLUMNS, args.MAX_ESSAY_LENGTH, tokenizer, args.MIN_SCORE, args.MAX_SCORE)
va_essays, va_true_scores, va_norm_scores = encode_essay_data(args.VALIDATION_DATA_FILE, args.SCORE_COLUMNS, args.MAX_ESSAY_LENGTH, tokenizer, args.MIN_SCORE, args.MAX_SCORE)

embeddings_matrix = get_word_embeddings_matrix(load_word_embeddings_dict(args.WORD_EMB_FILE), tokenizer.word_index, args.EMBEDDING_SIZE)

print('-------- -------- Model generation')
model = generate_model(vocab_size, args.EMBEDDING_SIZE, args.MAX_ESSAY_LENGTH, embeddings_matrix, args.LOSS_WEIGHTS)

# save the model
model_json = model.to_json()
with open('%s/%s' % (args.OUTPUT_DIR, args.MODEL_DEF_FILE), 'w') as json_file:
    json_file.write(model_json)

# write to stdout
sys.stdout.flush()

print('-------- -------- Training')
stats = Stats(len(args.SCORE_COLUMNS))
for epoch in range(args.NUM_EPOCHS):
    print('-------- -------- Epoch = %d' % epoch)

    print('-------- Fitting Model')
    hist = model.fit(tr_essays, tr_norm_scores, epochs=1, verbose=0, validation_data=(va_essays, va_norm_scores))

    # keep track of losses
    stats.add_losses(hist.history['loss'][0], hist.history['val_loss'][0])

    print('-------- Validating Model')
    stats.add_qwks(get_qwk(model, tr_essays, tr_true_scores, args.MIN_SCORE, args.MAX_SCORE), get_qwk(model, va_essays, va_true_scores, args.MIN_SCORE, args.MAX_SCORE))
    
    print('-------- Log')
    stats.print_log()

    stats.saveplots(args.OUTPUT_DIR, args.STATS_GRAPH_FILE)

    print('-------- Saving Model')
    # save model if it has best validation accuracy or training accuracy until now
    if stats.do_save_model(epoch):
        model.save_weights('%s/model%d.h5' % (args.OUTPUT_DIR, epoch))

    # write to stdout
    sys.stdout.flush()

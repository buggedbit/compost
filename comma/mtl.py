import argparse
import sys
import os
import glob
from models import generate_model
from preprocessor import generate_tokenizer_on_all_essays, encode_essay_data, load_word_embeddings_dict, get_word_embeddings_matrix
import numpy as np
from utils import Stats
from qwk import calc_qwk

# arguments
parser = argparse.ArgumentParser()
# required args
parser.add_argument('--TRAINING_DATA_FILE', required=True)
parser.add_argument('--VALIDATION_DATA_FILE', required=True)
parser.add_argument('--WORD_EMB_FILE', required=True)
parser.add_argument('--OUTPUT_DIR', required=True)
parser.add_argument('--NUM_EPOCHS', required=True, type=int)
parser.add_argument('--OVERALL_SCORE_COLUMN', required=True, type=int)
parser.add_argument('--ATTR_SCORE_COLUMNS', required=True, type=int, nargs='+')
parser.add_argument('--OVERALL_LOSS_WEIGHT', required=True, type=float)
parser.add_argument('--ATTR_LOSS_WEIGHTS', required=True, type=float, nargs='+')
parser.add_argument('--OVERALL_MIN_SCORE', required=True, type=int)
parser.add_argument('--OVERALL_MAX_SCORE', required=True, type=int)
parser.add_argument('--ATTR_MIN_SCORE', required=True, type=int)
parser.add_argument('--ATTR_MAX_SCORE', required=True, type=int)
# optional args
parser.add_argument('--MAX_ESSAY_LENGTH', type=int, default=2000)
parser.add_argument('--EMBEDDING_SIZE', type=int, default=300)
parser.add_argument('--LOG_FILE', default='log.txt')
parser.add_argument('--TRAINING_STATS_FILE', default='training_stats.txt')
parser.add_argument('--VOCAB_FILE', default='data/vocab_db.txt')
parser.add_argument('--MODEL_DEF_FILE', default='model.json')
parser.add_argument('--NUM_CONV_FILTERS', type=int, default=100)
parser.add_argument('--CONV_WINDOW_LENGTH', type=int, default=3)
parser.add_argument('--NUM_LSTM_NODES', type=int, default=100)
parser.add_argument('--DROPOUT_PROB', type=float, default=0)
args = parser.parse_args()

# assert output dir exists
assert (os.path.isdir(args.OUTPUT_DIR))
# assert no. of attr score cols == no. attr loss weights
assert (len(args.ATTR_SCORE_COLUMNS) == len(args.ATTR_LOSS_WEIGHTS))
# assert loss weights of overall + attr == 1
assert ((np.sum(args.ATTR_LOSS_WEIGHTS) + args.OVERALL_LOSS_WEIGHT) == 1)

# redirect stdout to logfile
sys.stdout = open('{}/{}'.format(args.OUTPUT_DIR, args.LOG_FILE), 'w')

# log arguments
print(args)
sys.stdout.flush()

# data pre-processing and preparation
# tokenizer prep
tokenizer_files = [args.VOCAB_FILE, ]
for propmt in range(1, 9):
    for fold in range(5):
        tokenizer_files.append('data/prompts-and-folds/Prompt-{}-Train-{}.csv'.format(propmt, fold))
        tokenizer_files.append('data/prompts-and-folds/Prompt-{}-Test-{}.csv'.format(propmt, fold))

print('Files used for generating tokenizer: {}'.format(tokenizer_files))
tokenizer = generate_tokenizer_on_all_essays(tuple(tokenizer_files))
vocab_size = len(tokenizer.word_index) + 1

# data extraction
tr_essays, tr_true_scores, tr_norm_scores = encode_essay_data(args.TRAINING_DATA_FILE, args.OVERALL_SCORE_COLUMN, args.ATTR_SCORE_COLUMNS, args.MAX_ESSAY_LENGTH, tokenizer, args.OVERALL_MIN_SCORE, args.OVERALL_MAX_SCORE, args.ATTR_MIN_SCORE, args.ATTR_MAX_SCORE)
va_essays, va_true_scores, va_norm_scores = encode_essay_data(args.VALIDATION_DATA_FILE, args.OVERALL_SCORE_COLUMN, args.ATTR_SCORE_COLUMNS, args.MAX_ESSAY_LENGTH, tokenizer, args.OVERALL_MIN_SCORE, args.OVERALL_MAX_SCORE, args.ATTR_MIN_SCORE, args.ATTR_MAX_SCORE)

# embeddings extraction
embeddings_matrix = get_word_embeddings_matrix(load_word_embeddings_dict(args.WORD_EMB_FILE, args.EMBEDDING_SIZE), tokenizer.word_index, args.EMBEDDING_SIZE)

# model generation
model = generate_model(vocab_size, args.EMBEDDING_SIZE, args.MAX_ESSAY_LENGTH, embeddings_matrix, args.OVERALL_LOSS_WEIGHT, args.ATTR_LOSS_WEIGHTS,
                        args.NUM_CONV_FILTERS, args.CONV_WINDOW_LENGTH, args.NUM_LSTM_NODES, args.DROPOUT_PROB)
print(model.summary())
model_json = model.to_json()
with open('{}/{}'.format(args.OUTPUT_DIR, args.MODEL_DEF_FILE), 'w') as json_file:
    json_file.write(model_json)
sys.stdout.flush()
sys.stdout.close()

stats = Stats(len(args.ATTR_SCORE_COLUMNS) + 1)
for epoch in range(args.NUM_EPOCHS):
    # fit the model
    hist = model.fit(tr_essays, tr_norm_scores, epochs=1, verbose=0, validation_data=(va_essays, va_norm_scores))
    # keep track of losses
    stats.add_losses(hist.history['loss'][0], hist.history['val_loss'][0])
    stats.add_qwks(calc_qwk(model, tr_essays, tr_true_scores, args.OVERALL_MIN_SCORE, args.OVERALL_MAX_SCORE, args.ATTR_MIN_SCORE, args.ATTR_MAX_SCORE),
                   calc_qwk(model, va_essays, va_true_scores, args.OVERALL_MIN_SCORE, args.OVERALL_MAX_SCORE, args.ATTR_MIN_SCORE, args.ATTR_MAX_SCORE))
    # save model if it has best validation accuracy in any attribute until now
    attr_indices = stats.attr_indices_with_best_va_qwk(epoch)
    for attr_index in attr_indices:
        for prev_best_model in glob.glob('{}/best-model-for-attr-{}-*.h5'.format(args.OUTPUT_DIR, attr_index)):
          os.remove(prev_best_model)
        model.save_weights('{}/best-model-for-attr-{}-@-epoch-{}.h5'.format(args.OUTPUT_DIR, attr_index, epoch))
    # print log and save plots
    stats.save_plots(args.OUTPUT_DIR)
    sys.stdout = open('{}/{}'.format(args.OUTPUT_DIR, args.TRAINING_STATS_FILE), 'w')
    print('# Epoch {} completed. Model fitted, validated and saved'.format(epoch))
    stats.print_log()
    # write to stdout
    sys.stdout.flush()
    sys.stdout.close()

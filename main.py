import argparse
import sys
import os
import glob
from model_generator import generate_model
from preprocessor import generate_tokenizer_on_all_essays, encode_essay_data, load_word_embeddings_dict, get_word_embeddings_matrix
import numpy as np
from utils import Stats, get_qwk

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
parser.add_argument('--VOCAB_FILE', default='data/vocab_db.txt')
parser.add_argument('--MODEL_DEF_FILE', default='model.json')
parser.add_argument('--STATS_GRAPH_FILE', default='training_stats.png')
args = parser.parse_args()

# assert output dir exists
assert (os.path.isdir(args.OUTPUT_DIR))
assert (len(args.ATTR_SCORE_COLUMNS) == len(args.ATTR_LOSS_WEIGHTS))
assert ((np.sum(args.ATTR_LOSS_WEIGHTS) + args.OVERALL_LOSS_WEIGHT) == 1)

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

tr_essays, tr_true_scores, tr_norm_scores = encode_essay_data(args.TRAINING_DATA_FILE, args.OVERALL_SCORE_COLUMN, args.ATTR_SCORE_COLUMNS, args.MAX_ESSAY_LENGTH, tokenizer, args.OVERALL_MIN_SCORE, args.OVERALL_MAX_SCORE, args.ATTR_MIN_SCORE, args.ATTR_MAX_SCORE)
va_essays, va_true_scores, va_norm_scores = encode_essay_data(args.VALIDATION_DATA_FILE, args.OVERALL_SCORE_COLUMN, args.ATTR_SCORE_COLUMNS, args.MAX_ESSAY_LENGTH, tokenizer, args.OVERALL_MIN_SCORE, args.OVERALL_MAX_SCORE, args.ATTR_MIN_SCORE, args.ATTR_MAX_SCORE)

embeddings_matrix = get_word_embeddings_matrix(load_word_embeddings_dict(args.WORD_EMB_FILE), tokenizer.word_index, args.EMBEDDING_SIZE)

print('-------- -------- Model generation')
model = generate_model(vocab_size, args.EMBEDDING_SIZE, args.MAX_ESSAY_LENGTH, embeddings_matrix, args.OVERALL_LOSS_WEIGHT, args.ATTR_LOSS_WEIGHTS)

# save the model
model_json = model.to_json()
with open('%s/%s' % (args.OUTPUT_DIR, args.MODEL_DEF_FILE), 'w') as json_file:
    json_file.write(model_json)

# write to stdout
sys.stdout.flush()

print('-------- -------- Training')
stats = Stats(len(args.ATTR_SCORE_COLUMNS) + 1)
for epoch in range(args.NUM_EPOCHS):
    print('-------- -------- Epoch = %d' % epoch)

    print('-------- Fitting Model')
    hist = model.fit(tr_essays, tr_norm_scores, epochs=1, verbose=0, validation_data=(va_essays, va_norm_scores))

    # keep track of losses
    stats.add_losses(hist.history['loss'][0], hist.history['val_loss'][0])

    print('-------- Validating Model')
    stats.add_qwks(get_qwk(model, tr_essays, tr_true_scores, args.OVERALL_MIN_SCORE, args.OVERALL_MAX_SCORE, args.ATTR_MIN_SCORE, args.ATTR_MAX_SCORE),
                   get_qwk(model, va_essays, va_true_scores, args.OVERALL_MIN_SCORE, args.OVERALL_MAX_SCORE, args.ATTR_MIN_SCORE, args.ATTR_MAX_SCORE))

    print('-------- Log')
    stats.print_log()

    stats.saveplots(args.OUTPUT_DIR, args.STATS_GRAPH_FILE)

    print('-------- Saving Model')
    # save model if it has best validation accuracy in any attribute until now
    attr_indices = stats.attr_indices_with_best_va_qwk(epoch)
    for attr_index in attr_indices:
        for prev_best_model in glob.glob('%s/best-model-for-attr-%d-*.h5' % (args.OUTPUT_DIR, attr_index)):
          os.remove(prev_best_model)
        model.save_weights('%s/best-model-for-attr-%d-@-epoch-%d.h5' % (args.OUTPUT_DIR, attr_index, epoch))

    # write to stdout
    sys.stdout.flush()

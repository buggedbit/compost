import sys
import os
from model_generator import generate_model
from preprocessor import generate_tokenizer_on_all_essays, encode_essay_data, load_word_embeddings_dict, \
    get_word_embeddings_matrix
from quadratic_weighted_kappa import quadratic_weighted_kappa
import numpy as np
import matplotlib
matplotlib.use("pdf")
from matplotlib import pyplot as plt

def get_qwk(model, essays, true_scores, min_score, max_score):
    predicted_n_scores = model.predict(essays, verbose=0)
    predicted_n_scores = np.reshape(predicted_n_scores, predicted_n_scores.shape[0])
    predicted_t_scores = np.round(min_score + predicted_n_scores * max_score)
    qwk = quadratic_weighted_kappa(predicted_t_scores, true_scores, min_rating=min_score, max_rating=max_score)
    return qwk

# hyper parameters
assert(len(sys.argv) == 8)
NUM_EPOCHS = int(sys.argv[1])
MIN_SCORE = int(sys.argv[2])
MAX_SCORE = int(sys.argv[3])
TRAINING_DATA_FILE = sys.argv[4]
VALIDATION_DATA_FILE = sys.argv[5]
WORD_EMB_FILE = sys.argv[6]
OUTPUT_DIR = sys.argv[7]

assert(os.path.isdir(OUTPUT_DIR))
MAX_ESSAY_LENGTH = 2000
EMBEDDING_SIZE = 300

# log file
sys.stdout = open('%s/log.txt' % OUTPUT_DIR, 'w')

# pre processing
print('-------- -------- Pre Processing')
tokenizer = generate_tokenizer_on_all_essays(('data/vocab_db.txt',))
vocab_size = len(tokenizer.word_index) + 1
tr_essays, tr_n_scores, tr_t_scores = encode_essay_data(TRAINING_DATA_FILE, MAX_ESSAY_LENGTH, tokenizer)
va_essays, va_n_scores, va_t_scores = encode_essay_data(VALIDATION_DATA_FILE, MAX_ESSAY_LENGTH, tokenizer)
word_embeddings_dict = load_word_embeddings_dict(WORD_EMB_FILE)
embeddings_matrix = get_word_embeddings_matrix(word_embeddings_dict, tokenizer.word_index, EMBEDDING_SIZE)

print('-------- -------- Model generation')
model = generate_model(vocab_size, MAX_ESSAY_LENGTH, embeddings_matrix)

# save the model
model_json = model.to_json()
with open('%s/model.json' % OUTPUT_DIR, 'w') as json_file:
    json_file.write(model_json)

# write to stdout
sys.stdout.flush()

tr_losses = []
va_losses = []
tr_qwks = []
va_qwks = []
for epoch in range(NUM_EPOCHS):
    print('-------- -------- Epoch = %d' % epoch)

    print('         -------- Fitting Model')
    hist = model.fit(tr_essays, tr_n_scores, epochs=1, verbose=0, validation_data=(va_essays, va_n_scores))

    # Calculate TR LOSS
    loss = hist.history['loss'][0]
    tr_losses.append(loss)
    print('tr_loss =', loss)
    # Calculate VA LOSS
    loss = hist.history['val_loss'][0]
    va_losses.append(loss)
    print('va_loss =', loss)

    print('         -------- Validating Model')
    qwk = get_qwk(model, va_essays, va_t_scores, MIN_SCORE, MAX_SCORE)
    # Calculate TR QWK
    qwk = get_qwk(model, tr_essays, tr_t_scores, MIN_SCORE, MAX_SCORE)
    tr_qwks.append(qwk)
    print('tr_qwk =', qwk)
    # Calculate VA QWK
    va_qwks.append(qwk)
    print('va_qwk =', qwk)
    # max VA QWK until now
    print('max va_qwk until now = %f @ %d epoch' % (va_qwks[np.argmax(va_qwks)], np.argmax(va_qwks)))

    print('         -------- Cumulative log')
    print('training_qwks = ', tr_qwks)
    print('training_losses = ', tr_losses)
    print('validation_qwks = ', va_qwks)
    print('validation_losses = ', va_losses)

    # save accuracy and weights
    print('         -------- Saving Model')
    # save model if it has best validation accuracy or training accuracy until now
    if epoch == np.argmax(va_qwks) or epoch == np.argmax(tr_qwks):
        model.save_weights('%s/model%d.h5' % (OUTPUT_DIR, epoch))

    # write to stdout
    sys.stdout.flush()

print('training_qwks = ', tr_qwks)
print('training_losses = ', tr_losses)
print('validation_qwks = ', va_qwks)
print('validation_losses = ', va_losses)
print('max_tr_qwk = %f @ %d epoch' % (tr_qwks[np.argmax(tr_qwks)], np.argmax(tr_qwks)))
print('max_va_qwk = %f @ %d epoch' % (va_qwks[np.argmax(va_qwks)], np.argmax(va_qwks)))

epochs = [i for i in range(NUM_EPOCHS)]
baseline = [0 for i in range(NUM_EPOCHS)]
plt.title('max va qwk = %f @ %d epoch\n max tr qwk = %f @ %d epoch' % (va_qwks[np.argmax(va_qwks)], np.argmax(va_qwks), tr_qwks[np.argmax(tr_qwks)], np.argmax(tr_qwks)))
plt.xlabel("epochs")
plt.ylabel("value")
plt.plot(epochs, baseline, '-', color='black')
plt.plot(epochs, tr_qwks)
plt.plot(epochs, va_qwks)
plt.plot(epochs, tr_losses)
plt.plot(epochs, va_losses)
plt.gca().legend(('y=0','training QWK', 'validation QWK', 'training loss', 'validation loss'))
plt.savefig('%s/trainingStats.png' % OUTPUT_DIR)

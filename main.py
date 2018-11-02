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
assert(len(sys.argv) == 6)
num_epochs = int(sys.argv[1])
training_data_file = sys.argv[2]
validation_data_file = sys.argv[3]
word_embeddings_file = sys.argv[4]
output_dir = sys.argv[5]
assert(os.path.isdir(output_dir))
MAX_ESSAY_LENGTH = 2000
EMBEDDING_SIZE = 300

# log file
sys.stdout = open('%s/log.txt' % output_dir, 'w')

# pre processing
print('-------- -------- Pre Processing')
tokenizer = generate_tokenizer_on_all_essays(('data/vocab_db.txt',))
vocab_size = len(tokenizer.word_index) + 1
tr_essays, tr_n_scores, tr_t_scores = encode_essay_data(training_data_file, MAX_ESSAY_LENGTH, tokenizer)
va_essays, va_n_scores, va_t_scores = encode_essay_data(validation_data_file, MAX_ESSAY_LENGTH, tokenizer)
word_embeddings_dict = load_word_embeddings_dict(word_embeddings_file)
embeddings_matrix = get_word_embeddings_matrix(word_embeddings_dict, tokenizer.word_index, EMBEDDING_SIZE)

print('-------- -------- Model generation')
model = generate_model(vocab_size, MAX_ESSAY_LENGTH, embeddings_matrix)

# save the model
model_json = model.to_json()
with open('%s/model.json' % output_dir, 'w') as json_file:
    json_file.write(model_json)

tr_losses = []
va_losses = []
tr_qwks = []
va_qwks = []
for epoch in range(num_epochs):
    print('-------- -------- Epoch = %d' % epoch)

    print('         -------- Fitting Model')
    hist = model.fit(tr_essays, tr_n_scores, epochs=1, verbose=0, validation_data=(va_essays, va_n_scores))
    loss = hist.history['loss'][0]
    tr_losses.append(loss)
    print('tr_loss =', loss)

    loss = hist.history['val_loss'][0]
    va_losses.append(loss)
    print('va_loss =', loss)

    print('         -------- Validating Model')
    qwk = get_qwk(model, va_essays, va_t_scores, 0, 3)
    va_qwks.append(qwk)
    print('va_qwk =', qwk)

    qwk = get_qwk(model, tr_essays, tr_t_scores, 0, 3)
    tr_qwks.append(qwk)
    print('tr_qwk =', qwk)
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
        model.save_weights('%s/model%d.h5' % (output_dir, epoch))

    # write to stdout
    sys.stdout.flush()

print('training_qwks = ', tr_qwks)
print('training_losses = ', tr_losses)
print('validation_qwks = ', va_qwks)
print('validation_losses = ', va_losses)
print('max tr qwk = %f @ %d epoch' % (tr_qwks[np.argmax(tr_qwks)], np.argmax(tr_qwks)))
print('max va qwk = %f @ %d epoch' % (va_qwks[np.argmax(va_qwks)], np.argmax(va_qwks)))

epochs = [i for i in range(num_epochs)]
baseline = [0 for i in range(num_epochs)]
plt.title('max va qwk = %f @ %d epoch\n max tr qwk = %f @ %d epoch' % (va_qwks[np.argmax(va_qwks)], np.argmax(va_qwks), tr_qwks[np.argmax(tr_qwks)], np.argmax(tr_qwks)))
plt.xlabel("epochs")
plt.ylabel("value")
plt.plot(epochs, baseline, '-', color='black')
plt.plot(epochs, tr_qwks)
plt.plot(epochs, va_qwks)
plt.plot(epochs, tr_losses)
plt.plot(epochs, va_losses)
plt.gca().legend(('y=0','training QWK', 'validation QWK', 'training loss', 'validation loss'))
plt.savefig('%s/trainingStats.png' % output_dir)

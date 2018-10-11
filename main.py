import sys
from model_generator import generate_model
from preprocessor import generate_tokenizer_on_all_essays, encode_essay_data, load_word_embeddings_dict, \
    get_word_embeddings_matrix
from quadratic_weighted_kappa import quadratic_weighted_kappa
import numpy as np

def get_qwk(model, essays, true_scores, min_score, max_score):
    predicted_va_n_scores = model.predict(essays, verbose=0)
    predicted_va_n_scores = np.reshape(predicted_va_n_scores, predicted_va_n_scores.shape[0])
    predicted_va_t_scores = np.round(min_score + predicted_va_n_scores * max_score)
    qwk = quadratic_weighted_kappa(predicted_va_t_scores, true_scores, min_rating=min_score, max_rating=max_score)
    return qwk

# hyper parameters
assert(len(sys.argv) == 2)
num_epochs = int(sys.argv[1])
MAX_ESSAY_LENGTH = 2000
EMBEDDING_SIZE = 300

# pre processing
print('-------- -------- Pre Processing -------- --------')
tokenizer = generate_tokenizer_on_all_essays(('data/vocab_db.txt',))
vocab_size = len(tokenizer.word_index) + 1
tr_essays, tr_n_scores, tr_t_scores = encode_essay_data('data/prompt3/Prompt-3-Train-0.csv', MAX_ESSAY_LENGTH, tokenizer)
# todo : change file path test -> validation
va_essays, va_n_scores, va_t_scores = encode_essay_data('data/prompt3/Prompt-3-Test-0.csv', MAX_ESSAY_LENGTH, tokenizer)
word_embeddings_dict = load_word_embeddings_dict('word_embeddings/glove.1M.300d.txt')
embeddings_matrix = get_word_embeddings_matrix(word_embeddings_dict, tokenizer.word_index, EMBEDDING_SIZE)

print('-------- -------- Model generation -------- --------')
model = generate_model(vocab_size, MAX_ESSAY_LENGTH, embeddings_matrix)

# save the model
model_json = model.to_json()
with open('model.json', 'w') as json_file:
    json_file.write(model_json)

qwks = []
for epoch in range(0, num_epochs):
    print('-------- -------- Epoch = %d -------- --------' % epoch)

    print('         -------- Fitting Model -------- --------')
    model.fit(tr_essays, tr_n_scores, epochs=1, verbose=0)

    print('         -------- Validating Model -------- --------')
    qwk = get_qwk(model, va_essays, va_t_scores, 0, 3)
    print('QWK =', qwk)
    qwks.append(qwk)
    print('max qwk until now = %f @ %d' % (qwks[np.argmax(qwks)], np.argmax(qwks)))

    # save accuracy and weights
    print('         -------- Saving Model -------- --------')
    # save model if it has best validation accuracy until now
    if epoch == np.argmax(qwks):
        model.save_weights('model%d.h5' % (epoch + 1))

print('validation qwks = ', qwks)
print('max qwk until now = %f @ %d' % (qwks[np.argmax(qwks)], np.argmax(qwks)))

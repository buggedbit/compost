import sys
from model_generator import generate_model
from preprocessor import generate_tokenizer_on_all_essays, encode_essay_data, load_word_embeddings_dict, \
    get_word_embeddings_matrix
from quadratic_weighted_kappa import quadratic_weighted_kappa
import numpy as np

def get_qwk(model, essays, true_scores, min_score, max_score):
    predicted_n_scores = model.predict(essays, verbose=0)
    predicted_n_scores = np.reshape(predicted_n_scores, predicted_n_scores.shape[0])
    predicted_t_scores = np.round(min_score + predicted_n_scores * max_score)
    qwk = quadratic_weighted_kappa(predicted_t_scores, true_scores, min_rating=min_score, max_rating=max_score)
    return qwk

# hyper parameters
assert(len(sys.argv) == 2)
num_epochs = int(sys.argv[1])
MAX_ESSAY_LENGTH = 2000
EMBEDDING_SIZE = 300

# pre processing
print('-------- -------- Pre Processing')
tokenizer = generate_tokenizer_on_all_essays(('data/vocab_db.txt',))
vocab_size = len(tokenizer.word_index) + 1
tr_essays, tr_n_scores, tr_t_scores = encode_essay_data('data/prompt3/Prompt-3-Train-0.csv', MAX_ESSAY_LENGTH, tokenizer)
# todo : change file path test -> validation
va_essays, va_n_scores, va_t_scores = encode_essay_data('data/prompt3/Prompt-3-Test-0.csv', MAX_ESSAY_LENGTH, tokenizer)
word_embeddings_dict = load_word_embeddings_dict('word_embeddings/glove.1M.300d.txt')
embeddings_matrix = get_word_embeddings_matrix(word_embeddings_dict, tokenizer.word_index, EMBEDDING_SIZE)

print('-------- -------- Model generation')
model = generate_model(vocab_size, MAX_ESSAY_LENGTH, embeddings_matrix)

# save the model
model_json = model.to_json()
with open('model.json', 'w') as json_file:
    json_file.write(model_json)

tr_qwks = []
va_qwks = []
for epoch in range(0, num_epochs):
    print('-------- -------- Epoch = %d' % epoch)

    print('         -------- Fitting Model')
    model.fit(tr_essays, tr_n_scores, epochs=1, verbose=0)

    print('         -------- Validating Model')
    qwk = get_qwk(model, va_essays, va_t_scores, 0, 3)
    va_qwks.append(qwk)
    print('va_qwk =', qwk)
    qwk = get_qwk(model, tr_essays, tr_t_scores, 0, 3)
    tr_qwks.append(qwk)
    print('tr_qwk =', qwk)
    print('max va_qwk until now = %f @ %d epoch' % (va_qwks[np.argmax(va_qwks)], np.argmax(va_qwks)))

    # save accuracy and weights
    print('         -------- Saving Model')
    # save model if it has best validation accuracy until now
    if epoch == np.argmax(va_qwks):
        model.save_weights('model%d.h5' % (epoch + 1))

print('train qwks = ', tr_qwks)
print('validation qwks = ', va_qwks)
print('max tr qwk = %f @ %d epoch' % (tr_qwks[np.argmax(tr_qwks)], np.argmax(tr_qwks)))
print('max va qwk = %f @ %d epoch' % (va_qwks[np.argmax(va_qwks)], np.argmax(va_qwks)))

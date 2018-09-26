from evaluator import evaluate
from model_generator import generate_model
from preprocessor import generate_tokenizer_on_all_essays, preprocess_essay_data, load_word_embeddings_dict, \
    get_word_embeddings_matrix

# meta data
MAX_ESSAY_LENGTH = 2000

# pre processing
print('-------- -------- Pre Processing -------- --------')
# todo : change file path test -> validation
tokenizer = generate_tokenizer_on_all_essays(('data/vocab_db.txt',))
vocab_size = len(tokenizer.word_index) + 1

tr_essays, tr_n_scores, tr_t_scores = preprocess_essay_data('data/prompt3/Prompt-3-Train-0.csv', MAX_ESSAY_LENGTH,
                                                            tokenizer)

# todo : change file path test -> validation
va_essays, va_n_scores, va_t_scores = preprocess_essay_data('data/prompt3/Prompt-3-Test-0.csv', MAX_ESSAY_LENGTH,
                                                            tokenizer)

word_embeddings_dict = load_word_embeddings_dict('word_embeddings/glove.1M.300d.txt')

embeddings_matrix = get_word_embeddings_matrix(word_embeddings_dict, 300, vocab_size, tokenizer.word_index)

print('-------- -------- Model generation -------- --------')
model = generate_model(vocab_size, MAX_ESSAY_LENGTH, embeddings_matrix)
# save the model
model_json = model.to_json()
with open('model.json', 'w') as json_file:
    json_file.write(model_json)

qwks = []
for epoch in range(0, 10):
    print('-------- -------- Epoch = %d -------- --------' % epoch)

    print('-------- -------- Fitting Model -------- --------')
    model.fit(tr_essays, tr_n_scores, epochs=1, verbose=1)

    print('-------- -------- Evaluating Model -------- --------')
    qwk = evaluate(model, va_essays, va_t_scores, min_score=0, max_score=3)

    print('QWK =', qwk)

    # save accuracy and weights
    print('-------- -------- Saving Model -------- --------')
    qwks.append(qwk)
    model.save_weights('model%d.h5' % epoch)

print(qwks)

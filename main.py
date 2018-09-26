from evaluator import evaluate
from model_generator import generate_model
from preprocessor import generate_tokenizer_on_all_essays, preprocess_essay_data, load_word_embeddings_dict, \
    get_word_embeddings_matrix

# meta data
MAX_ESSAY_LENGTH = 2000

# pre processing
print('-------- -------- Pre Processing -------- --------')
# todo : change file path test -> validation
tokenizer = generate_tokenizer_on_all_essays(('data/prompt3/Prompt-3-Train-0.csv', 'data/prompt3/Prompt-3-Test-0.csv'))
vocab_size = len(tokenizer.word_index) + 1

tr_essays, tr_n_scores, tr_t_scores = preprocess_essay_data('data/prompt3/Prompt-3-Train-0.csv', MAX_ESSAY_LENGTH,
                                                            tokenizer)

# todo : change file path test -> validation
va_essays, va_n_scores, va_t_scores = preprocess_essay_data('data/prompt3/Prompt-3-Test-0.csv', MAX_ESSAY_LENGTH,
                                                            tokenizer)

word_embeddings_dict = load_word_embeddings_dict('word_embeddings/glove.1M.300d.txt')

embeddings_matrix = get_word_embeddings_matrix(word_embeddings_dict, 300, vocab_size, tokenizer.word_index)

# print('-------- -------- Model generation -------- --------')
# model = generate_model(tr_vocab_size, MAX_ESSAY_LENGTH, embeddings_matrix)

# for epoch in range(0, 10):
#     print('-------- -------- Epoch = %d -------- --------' % epoch)
#     print('-------- -------- Fitting Model -------- --------')
#     # fit the model
#     model.fit(tr_essays, tr_n_scores, epochs=1, verbose=1)
#     qwk = evaluate(model, tr_essays, tr_t_scores, 0, 3)
#
#     if save_file is not None:
#         print('-------- -------- Saving Model -------- --------')
#         # save the model
#         model_json = model.to_json()
#         with open(save_file + '.json', 'w') as json_file:
#             json_file.write(model_json)
#         # serialize weights to HDF5
#         model.save_weights(save_file + '.h5')
#         print('Saved model to disk')

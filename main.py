from evaluator import evaluate
from model_generator import generate_model, fit_model
from preprocessor import preprocess_essay_data, load_word_embeddings, get_word_embeddings_matrix

# parameters
MAX_ESSAY_LENGTH = 2000

# pre processing
print('-------- -------- Pre Processing -------- --------')
essays, normalized_scores, true_scores, vocab_size, word_index = \
    preprocess_essay_data('data/prompt3/Prompt-3-Train-0.csv', max_length=MAX_ESSAY_LENGTH)

word_embeddings = load_word_embeddings('word_embeddings/glove.1M.300d.txt')

embeddings_matrix = get_word_embeddings_matrix(word_embeddings, vocab_size, 300, word_index)

# print('-------- -------- Model training -------- --------')
# model = generate_model(vocab_size, MAX_ESSAY_LENGTH, embeddings_matrix)
#
# for epoch in range(0, 10):
#     print('epoch =', epoch)
#     model = fit_model(model, essays, normalized_scores, epochs=1)
#     qwk = evaluate(model, essays, true_scores, 0, 3)

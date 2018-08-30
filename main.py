from evaluator import evaluate
from model_generator import generate_model, fit_model
from preprocessor import preprocess_essay_data, load_word_embeddings, get_word_embeddings_matrix

# parameters
MAX_ESSAY_LENGTH = 2000

# pre processing
print('-------- -------- Pre Processing -------- --------')
essays, normalized_scores, true_scores, vocab_size, word_index \
    = preprocess_essay_data('data/all.tsv', max_length=MAX_ESSAY_LENGTH)

word_embeddings = load_word_embeddings('word_embeddings/glove.100K.300d.txt')

embeddings_matrix = get_word_embeddings_matrix(word_embeddings, vocab_size, word_index)

print('-------- -------- Model training -------- --------')
model = generate_model(vocab_size, MAX_ESSAY_LENGTH, embeddings_matrix)
model = fit_model(model, essays, normalized_scores, epochs=10)

# model = generate_model(load_from_disk=True)

print('-------- -------- Evaluation -------- --------')
qwk = evaluate(model, essays, true_scores, 0, 60)

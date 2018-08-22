from keras.layers import Embedding, Flatten, Dense
from keras.models import Sequential

from preprocessing import preprocess_essay_data, load_word_embeddings, get_word_embeddings_matrix

# parameters
max_data_length = 2000

# pre processing
print('-------- -------- Pre Processing -------- --------')
data, labels, vocabulary_size, word_index = preprocess_essay_data('data/all.tsv', max_length=max_data_length)
word_embeddings = load_word_embeddings('word_embeddings/glove.100K.300d.txt')
embeddings_matrix = get_word_embeddings_matrix(word_embeddings, vocabulary_size, word_index)

# define model
model = Sequential()
model.add(Embedding(vocabulary_size, 300, weights=[embeddings_matrix], input_length=max_data_length, trainable=False))
model.add(Flatten())
model.add(Dense(1, activation='sigmoid'))

print('-------- -------- Compiling Model -------- --------')
# compile the model
model.compile(optimizer='adagrad', loss='mse', metrics=['mse'])
# summarize the model
print(model.summary())

print('-------- -------- Fitting Model -------- --------')
# fit the model
model.fit(data, labels, epochs=50, verbose=0)
# evaluate the model
loss, accuracy = model.evaluate(data, labels, verbose=0)
print('Accuracy: %f' % (accuracy * 100))

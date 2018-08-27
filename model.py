from keras.layers import Embedding, Flatten, Dense, Conv1D, LSTM, Dropout
from keras.models import Sequential

from custom_layers import Conv1DWithMasking, MeanOverTime, Attention
from preprocessing import preprocess_essay_data, load_word_embeddings, get_word_embeddings_matrix

# parameters
MAX_ESSAY_LENGTH = 2000

# pre processing
print('-------- -------- Pre Processing -------- --------')
data, labels, vocabulary_size, word_index = preprocess_essay_data('data/all.tsv', max_length=MAX_ESSAY_LENGTH)
word_embeddings = load_word_embeddings('word_embeddings/glove.100K.300d.txt')
embeddings_matrix = get_word_embeddings_matrix(word_embeddings, vocabulary_size, word_index)

# define model
print('-------- -------- Defining Model -------- --------')
model = Sequential()
model.add(Embedding(input_dim=vocabulary_size,
                    output_dim=300,
                    input_length=MAX_ESSAY_LENGTH,
                    weights=[embeddings_matrix],
                    trainable=True,
                    mask_zero=True,))
model.add(Conv1DWithMasking(filters=3,
                            kernel_size=3,
                            use_bias=True,
                            padding="same",))
model.add(LSTM(3, return_sequences=True,))
model.add(Dropout(0.1))
model.add(Attention(op='attmean', activation='tanh', init_stdev=0.01))
model.add(Dense(units=1, activation='sigmoid'))

print('-------- -------- Compiling Model -------- --------')
# compile the model
model.compile(optimizer='rmsprop', loss='mse', metrics=['mse'])
# summarize the model
print(model.summary())

print('-------- -------- Fitting Model -------- --------')
# fit the model
model.fit(data, labels, epochs=10, verbose=1)

print('-------- -------- Evaluating Model -------- --------')
# evaluate the model
loss, accuracy = model.evaluate(data, labels, verbose=1)
print('Accuracy: %f' % (accuracy * 100))

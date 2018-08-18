import csv

from keras.layers import Embedding, Flatten, Dense
import numpy as np
from keras.models import Sequential
from keras.preprocessing.sequence import pad_sequences
from keras.preprocessing.text import Tokenizer

# define essays and normalized_scores
essays = []
normalized_scores = []
with open('data/all.tsv') as f:
    tsv = csv.reader(f, delimiter="\t", quotechar='"')
    # Skip header
    next(tsv, None)
    for values in tsv:
        essay = values[2]
        normalized_score = values[11]
        essays.append(essay)
        normalized_scores.append(normalized_score)

# prepare tokenizer
tokenizer = Tokenizer()
tokenizer.fit_on_texts(essays)
vocabulary_size = len(tokenizer.word_index) + 1
print 'vocabulary size = ', vocabulary_size
# print(t.word_index.items())

# integer encode the documents
encoded_docs = tokenizer.texts_to_sequences(essays)
# print(encoded_docs)

# pad documents to a max length of 4 words
max_length = 2000
padded_docs = pad_sequences(encoded_docs, maxlen=max_length, padding='post')
print(padded_docs)

# load the whole embedding into memory
embeddings_dict = dict()
with open('word_embeddings/glove.100K.300d.txt') as f:
    for line in f:
        values = line.split()
        word = values[0]
        coefs = np.asarray(values[1:], dtype='float32')
        embeddings_dict[word] = coefs
print('Loaded %s word vectors.' % len(embeddings_dict))

# create a weight matrix for words in training essays
embedding_matrix = np.zeros((vocabulary_size, 300))
for word, index in tokenizer.word_index.items():
    embedding_vector = embeddings_dict.get(word)
    if embedding_vector is not None:
        embedding_matrix[index] = embedding_vector
    else:
        raise Exception('embedding of word not found: ' + word)
print(embedding_matrix)
print('Created embeddings matrix.')

# # define model
# model = Sequential()
# e = Embedding(vocabulary_size, 300, weights=[embedding_matrix], input_length=4, trainable=True)
# model.add(e)
# model.add(Flatten())
# model.add(Dense(1, activation='sigmoid'))
# # compile the model
# model.compile(optimizer='adam', loss='binary_crossentropy', metrics=['acc'])
# # summarize the model
# print(model.summary())
# # fit the model
# model.fit(padded_docs, labels, epochs=50, verbose=0)
# # evaluate the model
# loss, accuracy = model.evaluate(padded_docs, labels, verbose=0)
# print('Accuracy: %f' % (accuracy * 100))

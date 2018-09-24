import csv
import re
import numpy as np
from keras.preprocessing.sequence import pad_sequences
from keras.preprocessing.text import Tokenizer


def preprocess_essay_data(filepath, max_length):
    essays = []
    normalized_scores = []
    true_scores = []
    with open(filepath) as f:
        tsv = csv.reader(f, delimiter="\t", quotechar='"')
        # skip header
        # next(tsv, None)
        for values in tsv:
            essay = values[2]
            essays.append(re.sub('(@\w+)', '', essay))
            true_score = values[6]
            true_scores.append(float(true_score))
            normalized_scores.append(float(true_score) / 3)

    essays = np.array(essays)
    normalized_scores = np.array(normalized_scores)

    # prepare tokenizer
    tokenizer = Tokenizer()
    tokenizer.fit_on_texts(essays)

    # integer encode essays
    encoded_essays = tokenizer.texts_to_sequences(essays)

    # pad essays to a max length
    padded_essays = pad_sequences(encoded_essays, maxlen=max_length, padding='post')

    # vocabulary size
    vocab_size = len(tokenizer.word_index) + 1

    print('---- ---- encoded essays and normalized_scores')
    print('essays shape = ', padded_essays.shape, ' normalized_scores shape = ', normalized_scores.shape,
          'vocab_size = ', vocab_size)

    return padded_essays, normalized_scores, true_scores, vocab_size, tokenizer.word_index


def load_word_embeddings(filepath):
    """
    loads the whole embedding into memory
    """
    word_embeddings = dict()
    with open(filepath) as f:
        for line in f:
            try:
                values = line.split()
                word = values[0]
                word_embedding = np.array(values[1:], dtype='float32')
                word_embeddings[word] = word_embedding
            except ValueError:
                print(values, len(values))

    print('---- ---- loaded word embeddings into memory')
    print('no. of word embeddings = ', len(word_embeddings))

    return word_embeddings


def get_word_embeddings_matrix(word_embeddings, vocabulary_size, embedding_size, word_index):
    embeddings_matrix = np.zeros((vocabulary_size, embedding_size))
    spelling_mistakes = []
    for word, index in word_index.items():
        embedding = word_embeddings.get(word)
        if embedding is None:
            # 0 by default
            print(word)
            spelling_mistakes.append(word)
        else:
            embeddings_matrix[index] = embedding

    print('---- ---- created embeddings matrix')
    print('embeddings matrix shape =', embeddings_matrix.shape)
    print('found #spelling mistakes =', len(spelling_mistakes))
    print('%of words not found in word word embeddings =', len(spelling_mistakes) / vocabulary_size * 100)

    return embeddings_matrix

import csv
import re
import numpy as np
from keras.preprocessing.sequence import pad_sequences
from keras.preprocessing.text import Tokenizer


def preprocess_essay_data(filepath, max_length=2000):
    data = []
    labels = []
    with open(filepath) as f:
        tsv = csv.reader(f, delimiter="\t", quotechar='"')
        # skip header
        next(tsv, None)
        for values in tsv:
            essay = values[2]
            normalized_score = values[11]
            data.append(re.sub('(@\w+)', '', essay))
            labels.append(normalized_score)

    data = np.array(data)
    labels = np.array(labels)

    # prepare tokenizer
    tokenizer = Tokenizer()
    tokenizer.fit_on_texts(data)
    vocabulary_size = len(tokenizer.word_index) + 1
    # print('vocabulary size = ', vocabulary_size)
    # print(tokenizer.word_index)

    # integer encode data
    encoded_data = tokenizer.texts_to_sequences(data)
    # print('encoded data = ', encoded_data)

    # pad data to a max length
    padded_data = pad_sequences(encoded_data, maxlen=max_length, padding='post')
    # print('padded data = ', padded_data, padded_data.shape)

    return padded_data, vocabulary_size, labels

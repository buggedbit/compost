import csv
import re
import numpy as np
from keras.preprocessing.sequence import pad_sequences
from keras.preprocessing.text import Tokenizer


def generate_tokenizer_on_all_essays(all_files=()):
    all_essays = []
    for filepath in all_files:
        with open(filepath) as f:
            for essay in f:
                all_essays.append(re.sub('(@\w+)', '', essay))

            # tsv = csv.reader(f, delimiter="\t", quotechar='"')
            # # skip header
            # # next(tsv, None)
            # for values in tsv:
            #     essay = values[2]
            #     all_essays.append(re.sub('(@\w+)', '', essay))

    # prepare tokenizer
    tokenizer = Tokenizer()
    tokenizer.fit_on_texts(all_essays)

    # vocabulary size
    vocab_size = len(tokenizer.word_index) + 1
    print('vocab_size = ', vocab_size)

    return tokenizer


def encode_essay_data(filepath, score_columns, max_length, tokenizer, min_score, max_score):
    with open(filepath) as file:
        tsv = csv.reader(file, delimiter="\t", quotechar='"')
        tsv = list(tsv)
    
    essays = []
    true_score_tensor = []
    norm_score_tensor = []
    for col in score_columns:
        true_score_tensor.append([])
        norm_score_tensor.append([])
    # next(tsv, None) # skip header
    for i_essay, values in enumerate(tsv):
        # read essay
        # replace @... with ''
        essay = re.sub('(@\w+)', '', values[2])
        essays.append(essay)
        # read overall & attribute scores
        for i, col in enumerate(score_columns):
            true_score = float(values[col])
            true_score_tensor[i].append(true_score)
            norm_score = (true_score - min_score) / (max_score - min_score)
            norm_score_tensor[i].append(norm_score)

    for i, col in enumerate(score_columns):
        true_score_tensor[i] = np.asarray(true_score_tensor[i])
        norm_score_tensor[i] = np.asarray(norm_score_tensor[i])

    # integer encode
    encoded_essays = tokenizer.texts_to_sequences(essays)
    # pad/truncate to fixed length
    padded_essays = pad_sequences(encoded_essays, maxlen=max_length, padding='post')

    print('Preprocessed File=%s, Got essays_tensor of shape=%s & score_tensor of shape=%s' % (filepath, padded_essays.shape, np.asarray(norm_score_tensor).shape))

    return padded_essays, true_score_tensor, norm_score_tensor


def load_word_embeddings_dict(filepath):
    """
    loads the whole embedding into memory
    """
    word_embeddings = dict()
    with open(filepath) as f:
        for line in f:
            values = line.split()
            try:
                word = values[0]
                word_embedding = np.array(values[1:], dtype='float32')
                word_embeddings[word] = word_embedding
            except ValueError:
                # print(values, len(values))
                pass

    print('Loaded word embeddings into Memory. #WordEmbeddings=%d' % len(word_embeddings))

    return word_embeddings


def get_word_embeddings_matrix(word_embeddings_dict, word_index, embedding_size):
    vocab_size = len(word_index) + 1
    embeddings_matrix = np.zeros((vocab_size, embedding_size))
    spelling_mistakes = []
    for word, index in word_index.items():
        embedding = word_embeddings_dict.get(word)
        if embedding is None:
            # 0 by default
            # print(word)
            spelling_mistakes.append(word)
        else:
            embeddings_matrix[index] = embedding

    print('Created embeddings matrix. Embeddings Matrix shape =', embeddings_matrix.shape)
    print('#spelling mistakes found =', len(spelling_mistakes))
    print('%% Words Not found in Word Embeddings =', len(spelling_mistakes) / vocab_size * 100)

    return embeddings_matrix

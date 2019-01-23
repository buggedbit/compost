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


def encode_essay_data(filepath, overall_score_column, attr_score_columns, max_length, tokenizer, overall_min_score, overall_max_score, attr_min_score, attr_max_score):
    with open(filepath) as file:
        tsv = csv.reader(file, delimiter="\t", quotechar='"')
        tsv = list(tsv)
    
    essays = []
    true_score_tensor = []
    norm_score_tensor = []
    # overall score + attr scores list
    for _ in range(len(attr_score_columns) + 1):
        true_score_tensor.append([])
        norm_score_tensor.append([])
    # next(tsv, None) # skip header
    for i_essay, values in enumerate(tsv):
        # read essay
        # replace @... with ''
        essay = re.sub('(@\w+)', '', values[2])
        essays.append(essay)
        # read overall score
        true_score = float(values[overall_score_column])
        true_score_tensor[0].append(true_score)
        norm_score = (true_score - overall_min_score) / (overall_max_score - overall_min_score)
        norm_score_tensor[0].append(norm_score)
        # read attribute scores
        for i, col in enumerate(attr_score_columns):
            true_score = float(values[col])
            true_score_tensor[i + 1].append(true_score)
            norm_score = (true_score - attr_min_score) / (attr_max_score - attr_min_score)
            norm_score_tensor[i + 1].append(norm_score)

    for i in range(len(attr_score_columns) + 1):
        true_score_tensor[i] = np.asarray(true_score_tensor[i])
        norm_score_tensor[i] = np.asarray(norm_score_tensor[i])

    # integer encode
    encoded_essays = tokenizer.texts_to_sequences(essays)
    # pad/truncate to fixed length
    padded_essays = pad_sequences(encoded_essays, maxlen=max_length, padding='post')

    print('Preprocessed File=%s, Got essays_tensor of shape=%s & score_tensor of shape=%s' % (filepath, padded_essays.shape, np.asarray(norm_score_tensor).shape))

    return padded_essays, true_score_tensor, norm_score_tensor


def encode_essay_data_for_testing(filepath, overall_score_column, attr_score_columns, max_length, tokenizer):
    with open(filepath) as file:
        tsv = csv.reader(file, delimiter="\t", quotechar='"')
        tsv = list(tsv)
    
    essays = []
    essay_ids = []
    true_score_tensor = []
    # overall score + attr scores list
    for _ in range(len(attr_score_columns) + 1):
        true_score_tensor.append([])
    # next(tsv, None) # skip header
    for i_essay, values in enumerate(tsv):
        # read essay id
        essay_ids.append(int(values[0]))
        # read essay
        # replace @... with ''
        essay = re.sub('(@\w+)', '', values[2])
        essays.append(essay)
        # read overall score
        true_score = float(values[overall_score_column])
        true_score_tensor[0].append(true_score)
        # read attribute scores
        for i, col in enumerate(attr_score_columns):
            true_score = float(values[col])
            true_score_tensor[i + 1].append(true_score)

    for i in range(len(attr_score_columns) + 1):
        true_score_tensor[i] = np.asarray(true_score_tensor[i])

    # integer encode
    encoded_essays = tokenizer.texts_to_sequences(essays)
    # pad/truncate to fixed length
    padded_essays = pad_sequences(encoded_essays, maxlen=max_length, padding='post')

    print('Preprocessed File=%s, Got essays_tensor of shape=%s & score_tensor of shape=%s' % (filepath, padded_essays.shape, np.asarray(true_score_tensor).shape))

    return essay_ids, padded_essays, true_score_tensor


def load_word_embeddings_dict(filepath, word_embedding_size):
    """
    loads the whole embedding into memory
    """
    word_embeddings = dict()
    with open(filepath) as f:
        for line in f:
            values = line.split()
            try:
                word = ''.join(values[:len(values) - word_embedding_size])
                word_embedding = np.array(values[(len(values) - word_embedding_size):], dtype='float32')
                word_embeddings[word] = word_embedding
            except ValueError:
                print('causes error word_embedding: {}'.format(values))
                pass

    print('Loaded word embeddings into Memory. #WordEmbeddings=%d' % len(word_embeddings))

    return word_embeddings


def get_word_embeddings_matrix(word_embeddings_dict, word_index, embedding_size):
    vocab_size = len(word_index) + 1
    embeddings_matrix = np.zeros((vocab_size, embedding_size))
    spelling_mistakes = []
    
    for word, index in word_index.items():
        # stripping
        strip_keys = ['', '\'', '\"', '\”\“']
        found = False
        for strip_key in strip_keys:
            stripped_word = word.strip(strip_key)
            embedding = word_embeddings_dict.get(stripped_word)
            if embedding is not None:
                embeddings_matrix[index] = embedding
                if strip_key != '': print('stripped word {} to {}'.format(word, stripped_word))
                found = True
                break
        # apostropes
        if not found:
            aposes = ['\'s', '\"s']
            for apos in aposes:
                if word[-2:] == apos:
                    stripped_word = word[:-2]
                    embedding = word_embeddings_dict.get(stripped_word)
                    if embedding is not None:
                        embeddings_matrix[index] = embedding
                        if strip_key != '': print('stripped word {} to {}'.format(word, stripped_word))
                        found = True
                        break
        # spelling mistake 0 by default
        if not found:
            spelling_mistakes.append(word)
            print('spelling mistake: {}'.format(word))

    print('Created embeddings matrix. Embeddings Matrix shape =', embeddings_matrix.shape)
    print('#spelling mistakes found =', len(spelling_mistakes))
    print('%% Words Not found in Word Embeddings =', len(spelling_mistakes) / vocab_size * 100)

    return embeddings_matrix

from keras.layers import Embedding, Dense, LSTM, Dropout
from keras.models import Sequential

from custom_layers import Conv1DWithMasking, MeanOverTime


def generate_model(vocab_size, embeddings_size, max_essay_length, embeddings_matrix):
    # define model
    print('-------- -------- Defining Model')
    model = Sequential()
    model.add(Embedding(input_dim=vocab_size,
                        output_dim=embeddings_size,
                        input_length=max_essay_length,
                        weights=[embeddings_matrix],
                        trainable=False,
                        mask_zero=True, ))
    model.add(Conv1DWithMasking(filters=40,
                                kernel_size=3,
                                activation='sigmoid',
                                use_bias=True,
                                padding='same', ))
    model.add(LSTM(40, return_sequences=True, ))
    model.add(Dropout(0.1))
    model.add(MeanOverTime(mask_zero=True))
    model.add(Dense(units=1, activation='sigmoid'))

    print('-------- -------- Compiling Model')
    # compile the model
    model.compile(optimizer='rmsprop', loss='mse')
    # summarize the model
    print(model.summary())

    return model

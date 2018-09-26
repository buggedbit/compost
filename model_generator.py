from keras.layers import Embedding, Dense, LSTM, Dropout
from keras.models import Sequential, model_from_json

from custom_layers import Conv1DWithMasking, Attention


def generate_model(vocab_size, max_essay_length, embeddings_matrix):
    # define model
    print('-------- -------- Defining Model -------- --------')
    model = Sequential()
    model.add(Embedding(input_dim=vocab_size,
                        output_dim=300,
                        input_length=max_essay_length,
                        weights=[embeddings_matrix],
                        trainable=True,
                        mask_zero=True, ))
    model.add(Conv1DWithMasking(filters=300,
                                kernel_size=3,
                                use_bias=True,
                                padding="same", ))
    model.add(LSTM(300, return_sequences=True, ))
    # model.add(Dropout(0.1))
    # model.add(Attention(op='attmean', activation='tanh', init_stdev=0.01))
    model.add(Dense(units=1, activation='sigmoid'))

    print('-------- -------- Compiling Model -------- --------')
    # compile the model
    model.compile(optimizer='rmsprop', loss='mse', metrics=['mse'])
    # summarize the model
    print(model.summary())

    return model

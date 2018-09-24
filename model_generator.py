from keras.layers import Embedding, Dense, LSTM, Dropout
from keras.models import Sequential, model_from_json

from custom_layers import Conv1DWithMasking, Attention


def generate_model(vocab_size=None, max_essay_length=None, embeddings_matrix=None,
                   load_from_disk=False, model_file='model.json', weights_file='model.h5'):
    if load_from_disk:
        print('-------- -------- Loading Model -------- --------')
        # load model
        json_file = open(model_file, 'r')
        loaded_model_json = json_file.read()
        json_file.close()
        model = model_from_json(loaded_model_json,
                                custom_objects={'Conv1DWithMasking': Conv1DWithMasking, 'Attention': Attention})
        # load weights into new model
        model.load_weights(weights_file)
        print("Loaded model from disk")
    else:
        # define model
        print('-------- -------- Defining Model -------- --------')
        model = Sequential()
        model.add(Embedding(input_dim=vocab_size,
                            output_dim=300,
                            input_length=max_essay_length,
                            weights=[embeddings_matrix],
                            trainable=True,
                            mask_zero=True, ))
        model.add(Conv1DWithMasking(filters=3,
                                    kernel_size=3,
                                    use_bias=True,
                                    padding="same", ))
        model.add(LSTM(3, return_sequences=True, ))
        model.add(Dropout(0.1))
        model.add(Attention(op='attmean', activation='tanh', init_stdev=0.01))
        model.add(Dense(units=1, activation='sigmoid'))

        print('-------- -------- Compiling Model -------- --------')
        # compile the model
        model.compile(optimizer='rmsprop', loss='mse', metrics=['mse'])
    # summarize the model
    print(model.summary())

    return model


def fit_model(model, essays, normalized_scores, epochs=10, save_file=None):
    print('-------- -------- Fitting Model -------- --------')
    # fit the model
    model.fit(essays, normalized_scores, epochs=epochs, verbose=1)

    if save_file is not None:
        print('-------- -------- Saving Model -------- --------')
        # save the model
        model_json = model.to_json()
        with open(save_file + '.json', 'w') as json_file:
            json_file.write(model_json)
        # serialize weights to HDF5
        model.save_weights(save_file + '.h5')
        print('Saved model to disk')

    return model

from keras.models import Model
from keras.layers import *
from custom_layers import Conv1DWithMasking, MeanOverTime


def generate_model(vocab_size, embeddings_size, max_essay_length, embeddings_matrix, overall_loss_weight, attr_loss_weights):
    inp = Input((2000,),)
    x = Embedding(input_dim=vocab_size,
                        output_dim=embeddings_size,
                        input_length=max_essay_length,
                        weights=[embeddings_matrix],
                        trainable=False,
                        mask_zero=True,) (inp)
    x = Conv1DWithMasking(filters=40,
                                kernel_size=3,
                                activation='sigmoid',
                                use_bias=True,
                                padding='same',) (x)
    x = LSTM(40, return_sequences=True, )(x)
    x = Dropout(0.1) (x)
    x = MeanOverTime(mask_zero=True) (x)
    attribute_scores = []
    for i in range(len(attr_loss_weights)):
        score = Dense(units=1, activation='sigmoid') (x)
        attribute_scores.append(score)
    
    x = Concatenate()(attribute_scores)
    overall_score = Dense(units=1) (x)
    
    model = Model(inp, [overall_score] + attribute_scores)

    # compile the model
    model.compile(optimizer='rmsprop', loss=['mse' for _ in range(len(attr_loss_weights) + 1)], loss_weights=[overall_loss_weight] + attr_loss_weights)
    # summarize the model
    print(model.summary())

    return model

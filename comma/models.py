from keras.models import Model
from keras.layers import *
from keras.optimizers import RMSprop

from layers import Conv1DWithMasking, MeanOverTime


def generate_model(vocab_size, emb_size, max_essay_length, emb_matrix, overall_loss_weight, attr_loss_weights,
                   num_conv_filters, conv_window, num_lstm_nodes, do_prob):
    inp = Input((max_essay_length,), )
    # Embedding layer
    x = Embedding(input_dim=vocab_size,
                  output_dim=emb_size,
                  input_length=max_essay_length,
                  weights=[emb_matrix],
                  trainable=False,
                  mask_zero=True, )(inp)
    # Conv layer
    x = Conv1DWithMasking(filters=num_conv_filters,
                          kernel_size=conv_window,
                          activation='sigmoid',
                          use_bias=True,
                          padding='same', )(x)
    # Bi-LTSM layers
    forward = LSTM(num_lstm_nodes, return_sequences=True, )(x)
    backward = LSTM(num_lstm_nodes, return_sequences=True, go_backwards=True)(x)
    # Dropout layers
    forward = Dropout(do_prob)(forward)
    backward = Dropout(do_prob)(backward)
    # Mean over time layers
    forward_mot = MeanOverTime(mask_zero=True)(forward)
    backward_mot = MeanOverTime(mask_zero=True)(backward)
    # Concatenate layer
    essay_repr = Concatenate()([forward_mot, backward_mot])
    # Attr score outputs
    attribute_scores = []
    for i in range(len(attr_loss_weights)):
        score = Dense(units=1, activation='sigmoid')(essay_repr)
        attribute_scores.append(score)
    # Concatenate attr scores and essay representation
    x = Concatenate()(attribute_scores + [essay_repr])
    # Over all score output
    overall_score = Dense(units=1, activation='sigmoid')(x)

    # Define and compile model
    model = Model(inp, [overall_score] + attribute_scores)
    optimizer = RMSprop(lr=0.001, rho=0.9, clipnorm=10)
    model.compile(optimizer=optimizer, loss=['mse' for _ in range(len(attr_loss_weights) + 1)],
                  loss_weights=[overall_loss_weight] + attr_loss_weights)

    return model

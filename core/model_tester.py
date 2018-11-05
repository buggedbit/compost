import sys
from preprocessor import generate_tokenizer_on_all_essays, encode_essay_data
from quadratic_weighted_kappa import quadratic_weighted_kappa
import numpy as np
from keras.models import model_from_json
from custom_layers import Conv1DWithMasking, Attention, MeanOverTime, FlattenWithMasking

assert (len(sys.argv) == 4)
model_def_file = sys.argv[1]
model_weights_file = sys.argv[2]
test_data_file = sys.argv[3]


def get_qwk(model, essays, true_scores, min_score, max_score):
    predicted_n_scores = model.predict(essays, verbose=1)
    predicted_n_scores = np.reshape(predicted_n_scores, predicted_n_scores.shape[0])
    predicted_t_scores = np.round(min_score + predicted_n_scores * max_score)
    qwk = quadratic_weighted_kappa(predicted_t_scores, true_scores, min_rating=min_score, max_rating=max_score)
    return qwk


# hyper parameters
MAX_ESSAY_LENGTH = 2000

# pre processing
print('-------- -------- Pre Processing')
tokenizer = generate_tokenizer_on_all_essays(('data/vocab_db.txt',))
essays, n_scores, t_scores = encode_essay_data(test_data_file, MAX_ESSAY_LENGTH, tokenizer)

print('-------- -------- Model loading')
model = None
# load the model
with open(model_def_file, 'r') as json_file:
    model = model_from_json(json_file.read(),
                            custom_objects={'Conv1DWithMasking': Conv1DWithMasking, 'MeanOverTime': MeanOverTime})
model.load_weights(model_weights_file)

print('         -------- Evaluating Model')
qwk = get_qwk(model, essays, t_scores, 0, 3)
print('qwk =', qwk)

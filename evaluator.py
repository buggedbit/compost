import numpy as np
from quadratic_weighted_kappa import quadratic_weighted_kappa


def evaluate(model, test_essays, true_scores, min_score=0, max_score=60):
    print('-------- -------- Evaluating Model -------- --------')
    # evaluate the model
    predicted_normalized_scores = model.predict(test_essays, verbose=1)
    # print('predicted_normalized_scores =', predicted_normalized_scores)
    predicted_normalized_scores = np.reshape(predicted_normalized_scores, predicted_normalized_scores.shape[0])
    predicted_scores = np.round(min_score + predicted_normalized_scores * (max_score - min_score))
    # print('predicted_scores =', predicted_scores)
    # print('true_scores =', true_scores)
    qwk = quadratic_weighted_kappa(predicted_scores, true_scores, min_rating=min_score, max_rating=max_score)
    print('QWK =', qwk)
    return qwk

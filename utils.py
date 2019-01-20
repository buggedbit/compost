import numpy as np
import matplotlib
matplotlib.use('pdf')
from matplotlib import pyplot as plt
from quadratic_weighted_kappa import quadratic_weighted_kappa


def get_qwk(model, essays, true_scores, overall_min_score, overall_max_score, attr_min_score, attr_max_score):
    pred_norm_score_tensor = model.predict(essays, verbose=0)
    # clipping in case of linear final activation
    for i, pred_norm_scores in enumerate(pred_norm_score_tensor):
        for j, score in enumerate(pred_norm_scores):
            if score > 1:
                pred_norm_score_tensor[i][j] = 1
            elif score < 0:
                pred_norm_score_tensor[i][j] = 0

    qwks = []
    # overall score
    pred_norm_scores = pred_norm_score_tensor[0]
    pred_norm_scores = np.reshape(pred_norm_scores, pred_norm_scores.shape[0])
    pred_true_scores = np.round(overall_min_score + pred_norm_scores * (overall_max_score - overall_min_score))
    qwk = quadratic_weighted_kappa(pred_true_scores, true_scores[0], min_rating=overall_min_score, max_rating=overall_max_score)
    qwks.append(qwk)
    # attribute scores
    for i in range(1, len(pred_norm_score_tensor)):
        pred_norm_scores = pred_norm_score_tensor[i]
        pred_norm_scores = np.reshape(pred_norm_scores, pred_norm_scores.shape[0])
        pred_true_scores = np.round(attr_min_score + pred_norm_scores * (attr_max_score - attr_min_score))
        qwk = quadratic_weighted_kappa(pred_true_scores, true_scores[i], min_rating=attr_min_score, max_rating=attr_max_score)
        qwks.append(qwk)
    return qwks


class Stats:
    def __init__(self, num_tasks):
        self.num_tasks = num_tasks
        self.tr_losses = []
        self.va_losses = []
        self.tr_qwk_tensor = []
        self.va_qwk_tensor = []
        for task in range(num_tasks):
            self.tr_qwk_tensor.append([])
            self.va_qwk_tensor.append([])

    def add_losses(self, tr_loss, va_loss):
        self.tr_losses.append(tr_loss)
        self.va_losses.append(va_loss)

    def add_qwks(self, tr_qwks, va_qwks):
        for i, tr_qwk in enumerate(tr_qwks):
            self.tr_qwk_tensor[i].append(tr_qwk)
        for i, va_qwk in enumerate(va_qwks):
            self.va_qwk_tensor[i].append(va_qwk)

    def attr_indices_with_best_va_qwk(self, epoch):
        ret = []
        for attr_index, va_qwks in enumerate(self.va_qwk_tensor):
            if epoch == np.argmax(va_qwks):
                ret.append(attr_index)
        return ret

    def print_log(self):
        for i in range(self.num_tasks):
            va_qwks = self.va_qwk_tensor[i]
            tr_qwks = self.tr_qwk_tensor[i]
            print('for task %d, max va qwk = %f @ %d epoch' % (i, va_qwks[np.argmax(va_qwks)], np.argmax(va_qwks)))
            print('for task %d, max tr qwk = %f @ %d epoch' % (i, tr_qwks[np.argmax(tr_qwks)], np.argmax(tr_qwks)))

        print('training_qwks = ', self.tr_qwk_tensor)
        print('validation_qwks = ', self.va_qwk_tensor)
        print('training_losses = ', self.tr_losses)
        print('validation_losses = ', self.va_losses)

    def saveplots(self, output_dir, image_name):
        plt.clf()
        axes = plt.gca()
        axes.set_ylim([-0.5, 1])
        # plot all metrics in a graph
        epochs = [i for i in range(len(self.tr_losses))]
        yequals0 = [0 for i in range(len(self.tr_losses))]
        plt.xlabel('epochs')
        plt.ylabel('value')
        # X axis
        plt.plot(epochs, yequals0, '-', color='black')

        title_string = ''
        legend = ['y=0']
        va_qwks = self.va_qwk_tensor[0]
        title_string += 'overall score task, max va qwk = %f @ %d epoch\n' % (va_qwks[np.argmax(va_qwks)], np.argmax(va_qwks))
        for i in range(self.num_tasks):
            tr_qwks = self.tr_qwk_tensor[i]
            va_qwks = self.va_qwk_tensor[i]
            # plot validation qwks
            plt.plot(epochs, va_qwks)
            legend += ['task %d va qwk' % i]
            # plot training qwks
            plt.plot(epochs, tr_qwks)
            legend += ['task %d tr qwk' % i]

        plt.title(title_string)

        plt.plot(epochs, self.tr_losses)
        legend += ['training loss']
        plt.plot(epochs, self.va_losses)
        legend += ['validation loss']
        plt.gca().legend(tuple(legend))
        plt.savefig('%s/%s' % (output_dir, image_name))

import matplotlib
import numpy as np

matplotlib.use('pdf')
from matplotlib import pyplot as plt


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
        print('training_qwks = {}'.format(self.tr_qwk_tensor))
        print('validation_qwks = {}'.format(self.va_qwk_tensor))
        print('training_losses = {}'.format(self.tr_losses))
        print('validation_losses = {}'.format(self.va_losses))
        for i in range(self.num_tasks):
            va_qwks = self.va_qwk_tensor[i]
            tr_qwks = self.tr_qwk_tensor[i]
            print('for task {}, max va qwk = {} @ {} epoch'.format(i, np.max(va_qwks), np.argmax(va_qwks)))
            print('for task {}, max tr qwk = {} @ {} epoch'.format(i, np.max(tr_qwks), np.argmax(tr_qwks)))

    def save_plots(self, output_dir):
        for i in range(self.num_tasks):
            plt.clf()
            plt.xlabel('epochs')
            plt.ylabel('value')

            axes = plt.gca()
            axes.set_ylim([-0.75, 1])

            epochs = [i for i in range(len(self.tr_losses))]
            yequals0 = [0 for i in range(len(self.tr_losses))]

            tr_qwks = self.tr_qwk_tensor[i]
            va_qwks = self.va_qwk_tensor[i]

            plt.plot(epochs, yequals0, '-', color='black')
            plt.plot(epochs, va_qwks, '-', color='blue')
            plt.plot(epochs, tr_qwks, '-', color='pink')
            plt.plot(epochs, self.tr_losses, '-', color='red')
            plt.plot(epochs, self.va_losses, '-', color='green')

            plt.title('Task {}, max va qwk = {} @ {} epoch\n'.format(i, np.max(va_qwks), np.argmax(va_qwks)))
            plt.savefig('{}/task_{}.svg'.format(output_dir, i))
            plt.savefig('{}/task_{}.png'.format(output_dir, i))

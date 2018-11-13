import numpy as np
import matplotlib
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

    def do_save_model(self, epoch):
        # save if any of the va qwk is better in this epoch
        for va_qwks in self.va_qwk_tensor:
            if epoch == np.argmax(va_qwks):
                return True
        return False

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

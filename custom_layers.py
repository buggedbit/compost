import keras.backend as backend
from keras.engine.topology import Layer
import numpy as np
import sys
from keras.layers import Conv1D, Flatten

class MeanOverTime(Layer):
    def __init__(self, mask_zero=True, **kwargs):
        self.mask_zero = mask_zero
        self.supports_masking = True
        super(MeanOverTime, self).__init__(**kwargs)

    def call(self, x, mask=None):
        if self.mask_zero:
            return backend.cast(x.sum(axis=1) / mask.sum(axis=1, keepdims=True), backend.floatx())
        else:
            return backend.mean(x, axis=1)

    def compute_output_shape(self, input_shape):
        return input_shape[0], input_shape[2]

    def compute_mask(self, x, mask=None):
        return None

    def get_config(self):
        config = {'mask_zero': self.mask_zero}
        base_config = super(MeanOverTime, self).get_config()
        return dict(list(base_config.items()) + list(config.items()))


class Conv1DWithMasking(Conv1D):
    def __init__(self, **kwargs):
        self.supports_masking = True
        super(Conv1DWithMasking, self).__init__(**kwargs)

    def compute_mask(self, inputs, mask=None):
        return mask


class FlattenWithMasking(Flatten):
    def __init__(self, **kwargs):
        self.supports_masking = True
        super(FlattenWithMasking, self).__init__(**kwargs)

    def compute_mask(self, inputs, mask=None):
        return mask

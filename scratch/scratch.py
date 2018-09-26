import numpy as np
from keras.preprocessing.sequence import pad_sequences
from keras.preprocessing.text import Tokenizer

essays = np.array(['This is train', 'This is test'])

# prepare tokenizer
tokenizer = Tokenizer()
tokenizer.fit_on_texts(essays)

print(tokenizer.word_index)

# integer encode essays
encoded_essays = tokenizer.texts_to_sequences(['This is train'])
padded_essays = pad_sequences(encoded_essays, maxlen=3, padding='post')
print(padded_essays)
encoded_essays = tokenizer.texts_to_sequences(['Test tesT train Train'])
padded_essays = pad_sequences(encoded_essays, maxlen=3, padding='post')
print(encoded_essays)
print(padded_essays)

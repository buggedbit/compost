#!/bin/bash

PROMPT_NO=3
EPOCHS=100
GPU_CORE=3

# todo: Before running, set the correct GPU No.
for FOLD_NO in 0 1 2 3 4
do
  mkdir -p results/prompt${PROMPT_NO}/fold${FOLD_NO}
  THEANO_FLAGS="device=gpu${GPU_CORE},floatX=float32" python3 main.py ${EPOCHS} data/train-and-test/Prompt-${PROMPT_NO}-Train-${FOLD_NO}.csv data/train-and-test/Prompt-${PROMPT_NO}-Test-${FOLD_NO}.csv ../glove.1M.300d.txt results/prompt${PROMPT_NO}/fold${FOLD_NO}
done

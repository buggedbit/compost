#! /bin/bash

GPU_NUM=3

PROMPT=1
OVERALL_SCORE_COLUMN='6'
ATTR_SCORE_COLUMNS='7 8 9 10 11'
OVERALL_MAX_SCORE=12
OVERALL_MIN_SCORE=2
ATTR_MIN_SCORE=1
ATTR_MAX_SCORE=6

for FOLD in `seq 0 4`; do
  echo -e "======== Fold ${FOLD} ========"
  MODEL_FILE="results/8thsem/final-model/${PROMPT}/${FOLD}/model.json"
  TEST_DATA_FILE="data/prompts-and-folds/Prompt-${PROMPT}-Test-${FOLD}.csv"
  OUTPUT_FILE_PREFIX="results/8thsem/predictions/${PROMPT}/F${FOLD}"

  MODEL_WEIGHT_FILES=""
  for ATTR in `seq 0 5`; do
    MODEL_WEIGHT_FILES+="$(find results/8thsem/final-model/${PROMPT}/${FOLD}/ -name best-model-for-attr-${ATTR}-@-epoch-*.h5) "
  done

  THEANO_FLAGS="device=gpu${GPU_NUM},floatX=float32" python3 tester.py --MODEL_FILE ${MODEL_FILE} --MODEL_WEIGHT_FILES ${MODEL_WEIGHT_FILES} --TEST_DATA_FILE ${TEST_DATA_FILE} --OUTPUT_FILE_PREFIX ${OUTPUT_FILE_PREFIX} --OVERALL_SCORE_COLUMN ${OVERALL_SCORE_COLUMN} --ATTR_SCORE_COLUMNS ${ATTR_SCORE_COLUMNS} --OVERALL_MIN_SCORE ${OVERALL_MIN_SCORE} --OVERALL_MAX_SCORE ${OVERALL_MAX_SCORE} --ATTR_MIN_SCORE ${ATTR_MIN_SCORE} --ATTR_MAX_SCORE ${ATTR_MAX_SCORE}

done

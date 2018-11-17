GPU=$1
PROPMT=$2
FOLD=$3
BESTMODEL=$4

THEANO_FLAGS="device=gpu${GPU},floatX=float32" python3 model_tester.py --MODEL_FILE results/STL/prompt${PROPMT}/fold${FOLD}/model.json --MODEL_WEIGHTS_FILE results/STL/prompt${PROPMT}/fold${FOLD}/model${BESTMODEL}.h5  --TEST_DATA_FILE data/train-and-test/Prompt-${PROPMT}-Test-${FOLD}.csv --OUTPUT_FILE results/STL/Predictions/prediction-${PROPMT}-${FOLD}.csv --OVERALL_SCORE_COLUMN 6 --OVERALL_MIN_SCORE 2 --OVERALL_MAX_SCORE 12 --ATTR_MIN_SCORE 2 --ATTR_MAX_SCORE 12

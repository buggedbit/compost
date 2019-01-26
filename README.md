# **,** (Comma)
* Comma is an automatic essay grader; i.e. comma assesses a given essay and assigns a score to it
* It does this via multi-task deep learning
* It is heavily inspired by Taghipour's deep neural model

# Training
* Comma is trained on the dataset published by Kaggle for ASAP competition conducted in 2012.
* Comma uses the enriched version of ASAP dataset published by the ASAP++ paper for gold scores of essay attributes (for content, organization etc..)

# ASAP dataset properties

| Prompt number | Overall score range | Attribute score range | No. Essays | Avg. Length |
|---------------|---------------------|-----------------------|------------|-------------|
|1              |  2 - 12             |         1 - 6         | 1783       |     350     |
|2              |  1 - 6              |         1 - 6         | 1800       |     350     |
|3              |  0 - 3              |         0 - 3         | 1726       |     150     |
|4              |  0 - 3              |         0 - 3         | 1772       |     150     |
|5              |  0 - 4              |         0 - 4         | 1805       |     150     |
|6              |  0 - 4              |         0 - 4         | 1800       |     150     |
|7              |  0 - 30             |         0 - 6         | 1569       |     250     |
|8              |  0 - 60             |         2 - 12        | 723        |     650     |

# How to train?
* `THEANO_FLAGS="device=gpu1,floatX=float32" python3 main.py --WORD_EMB_FILE /home/yashasvi/glove.1M.300d.txt --TRAINING_DATA_FILE data/prompts-and-folds/Prompt-1-Train-0.csv  --VALIDATION_DATA_FILE data/prompts-and-folds/Prompt-1-Test-0.csv --OVERALL_SCORE_COLUMN 6 --ATTR_SCORE_COLUMNS 7 8 9 10 11 --OVERALL_LOSS_WEIGHT 0.5 --ATTR_LOSS_WEIGHTS 0.1 0.1 0.1 0.1 0.1 --OVERALL_MIN_SCORE 2 --OVERALL_MAX_SCORE 12 --ATTR_MIN_SCORE 1 --ATTR_MAX_SCORE 6 --NUM_EPOCHS 100 --OUTPUT_DIR results/test-run-1-0-mot+attr+sigm+bilstm+periodgrace+tokensfromall+2/ 2>results/test-run-1-0-mot+attr+sigm+bilstm+periodgrace+tokensfromall+2/stderr &`

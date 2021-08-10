# comma

## description
- A predictor of natural language essay quality.
- The baseline is <https://aclanthology.org/D16-1193/>, which predicts overall quality of the essay.
- This builds on it using multi-task optimization, each task being an aspect of the essay.
- The data used is the enriched version of ASAP dataset (published by Kaggle for ASAP competition conducted in 2012) ASAP++ <https://aclanthology.org/L18-1187.pdf>.

### ASAP++ dataset properties
| Prompt number | Overall score range | Attribute score range | No. of attrs | No. Essays | Avg. Length |
|---------------|---------------------|-----------------------|--------------|------------|-------------|
|1              |  2 - 12             |         1 - 6         |      5       | 1783       |     350     |
|2              |  1 - 6              |         1 - 6         |      5       | 1800       |     350     |
|3              |  0 - 3              |         0 - 3         |      4       | 1726       |     150     |
|4              |  0 - 3              |         0 - 3         |      4       | 1772       |     150     |
|5              |  0 - 4              |         0 - 4         |      4       | 1805       |     150     |
|6              |  0 - 4              |         0 - 4         |      4       | 1800       |     150     |
|7              |  0 - 30             |         0 - 6         |      4       | 1569       |     250     |
|8              |  0 - 60             |         2 - 12        |      6       | 723        |     650     |


## roadmap
- A crude but working version of the idea (pre-processing, training from scratch weights, validation and testing) is implemented. See the code directly for more details.
- MTL
    - [x] run MTL model on all folds of prompt 4
    - [x] run MTL model on all prompts
    - [x] statistical significance tests
- Roadmap for MTL
    - [x] 0.5 + 0.5 model
    - [x] 0.6 + 0.1 + ... 0.1 model
    - [x] attr - dense - sigmoid model
    - [x] attr - dense - overall model
    - [x] fix the linear activation overflow bug
- Final Presentation & Report
    - [x] report
    - [x] presentation
    - [x] web interface
- [ ] fold distrubution used - same score distrubution as total smaller class classify correctly more QWK.
- [ ] NACL paper, Cross domain tests

## code
- Code is written in `python`.
- `keras` is used as deep learning library.

## usage
- To train use
```bash
    THEANO_FLAGS="device=gpu1,floatX=float32" python3 main.py \
        --WORD_EMB_FILE /home/yashasvi/glove.1M.300d.txt \
        --TRAINING_DATA_FILE data/prompts-and-folds/Prompt-1-Train-0.csv  \
        --VALIDATION_DATA_FILE data/prompts-and-folds/Prompt-1-Test-0.csv \
        --OVERALL_SCORE_COLUMN 6 \
        --ATTR_SCORE_COLUMNS 7 8 9 10 11 \
        --OVERALL_LOSS_WEIGHT 0.5 \
        --ATTR_LOSS_WEIGHTS 0.1 0.1 0.1 0.1 0.1 \
        --OVERALL_MIN_SCORE 2 \
        --OVERALL_MAX_SCORE 12 \
        --ATTR_MIN_SCORE 1 \
        --ATTR_MAX_SCORE 6 \
        --NUM_EPOCHS 100 \
        --OUTPUT_DIR results/test-run-1-0-mot+attr+sigm+bilstm+periodgrace+tokensfromall+2/ \
        2>results/test-run-1-0-mot+attr+sigm+bilstm+periodgrace+tokensfromall+2/stderr &
```

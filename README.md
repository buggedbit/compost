# **,** (Comma)
* Comma is an automatic essay grader; i.e. comma assesses a given essay and assigns a score to it
* It does this via multi-task deep learning
* It is heavily inspired by Taghipour's deep neural model

# Training
* Comma is trained on the dataset published by Kaggle for ASAP competition conducted in 2012.
* Comma uses the enriched version of ASAP dataset published by the ASAP++ paper for gold scores of essay attributes (for content, organization etc..)

## ASAP dataset properties

| Prompt number | Overall score range | Attribute score range |
|---------------|---------------------|-----------------------|
|1              |  2 - 12             |         1 - 6         |
|2              |  1 - 6              |         1 - 6         |
|3              |  0 - 3              |         0 - 3         |
|4              |  0 - 3              |         0 - 3         |
|5              |  0 - 4              |         0 - 4         |
|6              |  0 - 4              |         0 - 4         |
|7              |  0 - 30             |         0 - 6         |
|8              |  0 - 60             |         2 - 12        |

## todo
- [x] Tag spelling mistakes words as 0
- [ ] Make Trainable True in embeddings layer
- [ ] Conv1DWithMasking
- [ ] mask zero in all layers
- [ ] Aggregation layer
- [ ] Optimizer clipping
- [ ] QWK metric for evaluation
- [ ] Get all glove data to decrease spelling mistakes
- [ ] Tag named entities @ to a new encoding
- [ ] Tune params and train with all data

## questions

- [x] What does a convolutional layer do in NLP context? Specifically how does it convert a 2000 x 300 -> 2000 x 1 with one filter
- [x] Choice of hyperparatmeters loss optimizer metric (QWK?) activations (sigmoid or softmax)
- [x] Which RNN to use?
- [x] Is this classification or regression?
- [x] Overall score - Induvidual score relation (deterministic or learnable)
- [ ] How to proceed with more data once model is complete?
- [ ] Strange behaviour Softmax - no change in loss, sigmoid - decreasing loss?
- [ ] sigmoid/softmax activations?
# What is it?
A tool to take care of the mundane so that you can really focus on interesting things
* It will make your more organized, but actually decreases the effort in doing so
* It will enable you to be free of mind or stateless
    * The intention however is not to impose to forget everything, but to provide a means to swap out all the irrelevant information
* It can understand arbitrarily complex tasks, store and visualize them beautifully
* It really comes handy when you have a number of interdependent tasks dangling on your head

# Zen of Escape Errands
* _Live now. The future can wait_

# How to use?
* Escape Errand comes as a server client system
* The server is based on django framework
* The client has a web interface
* Dependencies
    * Look in `requirements.txt` for the list of all dependencies
    * `runtime.txt` contains the optimum version of python
* To run the server run the following commands in the project root dir
    * `python3 manage.py makemigrations core`
    * `python3 manage.py migrate`
    * `python3 manage.py runserver`

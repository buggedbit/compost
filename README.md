# Escape Errands
My solution to mundanity

# What is it?
A tool to take care of the mundane so that you can really focus on interesting things
* It will make your more organized, but actually decreases your effort in doing so
* It will enable you to be free of mind or stateless
    * The intention however is not make you to forget everything, but to provide a means to swap out all the currently irrelevant information stuff to a safe place
* It can understand arbitrarily complex tasks, store and visualize them beautifully
* It really comes in handy if you have a lot of interdependent tasks dangling over your head

# Zen of Escape Errands
_Live now. The future can wait_

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

* When the server is up and running, you can use the following urls to navigate the system
    * `{root-url}/` takes you to home page
        * Here you will be asked for a password for further access
    * `{root-url}/goal/glance` takes you to a glance view of your goals
        * This is also a powerful editor for your goals
        * A goal is simply a description with an (optional) deadline
        * Here you can search (using regex patterns), list, create, update and delete goals
        * You can set a goal as _achieved_ once its done
        * You can also link/de-link goals to create/break dependencies of one goal to another
        * The consistency of goal achievements and deadlines are automatically maintained
            * Consistency of goal deadlines means: deadline of child >= deadline of parent
            * Consistency of goal achievements means: child must be tagged as achieved only after all its parents are achieved
        * You can see the entire dependency graph of goal family rendered graphically as nodes and edges
        * You can do chain updates in a dependency graph
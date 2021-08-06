# Brain Dump
- A tool to dump and organize your thoughts.
- Break down arbitrarily complex tasks into graphs of simple atomic tasks to store and visualize them beautifully.

# The Zen
- Use your brain to think, not to remember!

# How to use?
* This is a server-client system.
* The server is based on django framework.
* The client has a React JS web interface.
* Dependencies
    * Look in `requirements.txt` for the list of all dependencies.
    * `runtime.txt` contains the optimum version of python.
* To run the server run the following commands in the project root dir.
    * `python3 manage.py makemigrations core`
    * `python3 manage.py migrate`
    * `python3 manage.py runserver`

* When the server is up and running, you can use the following urls to navigate the system.
    * `{root-url}/` takes you to home page.
        * Here you will be asked for a password for further access.
        * For now the password is hardcoded and is `airturtle`.
    * `{root-url}/goal/glance` takes you to a glance view of your goals
        * This is also a powerful editor for your goals.
        * A goal is simply a description with an (optional) deadline.
        * Here you can search (using regex patterns), list, create, update and delete goals.
        * You can set a goal as _achieved_ once its done.
        * You can also link/de-link goals to create/break dependencies of one goal to another.
        * The consistency of goal achievements and deadlines are automatically maintained.
            * Consistency of goal deadlines means: deadline of child >= deadline of parent.
            * Consistency of goal achievements means: child must be tagged as achieved only after all its parents are achieved.
        * You can see the entire dependency graph of goal family rendered graphically as nodes and edges.
        * You can do chain updates in a dependency graph.
    * `{root-url}/goal/snapshot_viewer` takes you to a glance your goals month-wise.

# todo
- [x] improve goal search app
	- [x] group by families
	- [x] fix sort order
	- [x] set bg of overdue goals to red
- [x] title
- [x] password
- [x] goal color Create & Update
- [ ] use map() in goalapis.py
- [x] snapshot viewer
    - [x] render snapshot month
    - [x] row major/ column major layout switch
    - [x] increasing color density of a day
    - [x] handle error/failed fetch
    - [x] highlight today
    - [x] styling
    - [x] change months
    - [x] day detail
- [ ] keyboard shortcuts
- [ ] Job model

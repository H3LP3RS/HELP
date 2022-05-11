## Forum Posting Protocol
The forum structure is the following:
Root
    CARDIOLOGY
        Post 1
            Reply 1 to Post 1
            Reply 2 to Post 1
        Post 2
    GYNECOLOGY

Now due to limitations in Firebase, the actual implementation of this structure is the following:

The forum database has several children, one per category defined in ForumCategory. In each of these
categories is a list of posts. Each post has a post id (thus a way to retrieve it from the database)
and a replies id which is where we store the replies to this post (since we can't have objects be
children of another object, we are obliged to store them "separately"). Additionally, to keep track
of all the posts in a single category, we also have a "posts" key in each category which lists the 
keys of all posts in the category.
This may be a little complicated as text so there is a diagram to represent the situation from above
but this time as it is actually stored in the database:

FORUM
    CARDIOLOGY
        Post 1: key = "3", repliesKey = "4"
        Post 2: key = "5", repliesKey = "6"
        4:
            Reply 1 to Post 1
            Reply 2 to Post 1
        posts:
            3
            5
    GYNECOLOGY
    
    



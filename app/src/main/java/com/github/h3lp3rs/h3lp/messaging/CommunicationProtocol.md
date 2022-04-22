## Communication Protocol

Notes: 
* In the explanation below, we'll use the term **helper** to indicate a person who, in the case 
  of an emergency, will go help the person in need and will use the term **helpee** to indicate the 
  person in need of help.
* We speak of conversation ids below, the reason why we need such ids is that to create a 
  conversation between two users, they need to agree to find a unique place on the database where
  they can both send their messages, this place is described by the unique conversation id. The goal
  of the protocol is then to be able to generate a unique, shared conversation id between the helpee
  and any helper willing to help without sharing personal information nor communication between the 
  helpers.


To keep the conversation anonymous, we have devised the following protocol:
1. When a user needs help, they generate a unique id (which we'll call helpeeId) and send it as 
   part of their emergency message
2. When potential helpers receive that emergency message and accept to go help, they each generate a 
   new unique conversation id (which we'll call conversationId_i for helper_i). conversationId_i 
   will serve as a conversation id for the conversation between helper_i and the person in need of 
   help. We require as many conversation ids as the number of helper since several helpers can come 
   help a single person in need and thus, the helpee will have a one-on-one conversation with each 
   of them.
3. Each helper sends their own conversation id (conversationId_i) to the database on the key called
   helpeeId to allow for the helpee to have access to every unique conversation id with every helper.
   Conversely, every helper already has the conversation id for their conversation with the helpee:
   conversationId_id, since they are the ones that generated it.
   
We now have a conversation between the helpee and any helper willing to help (without requiring the 
sharing of any personal information)!
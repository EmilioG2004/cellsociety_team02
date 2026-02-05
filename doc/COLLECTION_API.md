# Collections API Lab Discussion
### NAMES
Ixa Sigler, Emilio Garcia, Michael Lei
### TEAM 2



## In your experience using these collections, are they hard or easy to use?
In our experience, using Collections is fairly simple and straightforward. The methods feel intuitive
and it is very easy to implement and extend. One of the Collections API design themes was using lots 
of optional operators and keeping the number of required methods low. This allows for lots of 
flexibility in determining and deciding implementations. 

## In your experience using these collections, do you feel mistakes are easy to avoid?
In our experience using Collections, mistakes are very easy to avoid. We feel this way because the 
methods are extremely straightforward and intuitive. This makes avoiding mistakes quite easy, it 
almost feels natural to use this API. Conceptually, it also makes a lot of sense to group these 
similar Collections implementations together. 

## What methods are common to all collections (except Maps)?
They share every method in the Collections interface, including:
int size()
boolean isEmpty()
boolean contains(Object o)
Iterator<E> iterator()
boolean add (E e)
boolean remove (Object o)
boolean containsAll(Collection<?> c)
boolean addAll(Collection<? extends E> c)
boolean removeAll(Collection<?> c)
boolean retainAll(Collection<?> c)
void clear()
Object[] toArray()
<T> T[] toArray(T[] a)

## What is the purpose of each interface implemented by LinkedList?
Iterable<E> - Allows iteration with a for-each loop.
Collection<E> - Defines basic collection operations like add, remove, and size.
List<E> - Supports an ordered sequence with index-based access.
Queue<E> - Supports FIFO queue operations.
Deque<E> - Supports insertion and removal at both ends.

## How many different implementations are there for a Set?
3: HashSet, TreeSet, and LinkedHashSet

## What is the purpose of each superclass of PriorityQueue?
Object provides universal methods that all Java classes have. AbstractCollection adds the collection logic. AbstractQueue adds the queue logic on top of AbstractCollection

## What is the purpose of the Collections utility class?
The Collections class provides static utility methods for operating on and adapting collections, including algorithms, behavioral wrappers (unmodifiable, synchronized), and factory methods, without requiring changes to the underlying collection implementations.



## API Characterics applied to Collections API

 * Easy to learn
- Core interfaces (List, Set, Map, Queue) map to familiar data structures
- Consistent method naming across implementations (add, remove, contains, size)
- Common operations work the same way regardless of implementation choice
- Clear hierarchy: Collection -> List/Set/Queue makes relationships obvious
 * Encourages extension
- Abstract classes (AbstractList, AbstractSet, AbstractMap) provide skeletal implementations
- Only need to implement a few key methods to create custom collections
 * Leads to readable code
- Method names clearly express intent
- Programming to less specific interfaces allows us to avoid actually using multiple implementations in the top level code even if underneath the interfaces are different more specific implementations.
 * Hard to misuse
- Methods are simple and intuitive, nothing too complex
- There are few required methods, so optional methods allows implementations to be added with lots of flexibility
 
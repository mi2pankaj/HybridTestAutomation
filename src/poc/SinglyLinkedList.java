package poc;

public class SinglyLinkedList {

	static class Node { 

		private int data; 
		private Node next; 

		public Node(int data) { 
			this.data = data; 
		} 

		public int data() { 
			return data; 
		} 

		public Node next() {
			return next; 
		} 
	}

	private Node head; 

	public SinglyLinkedList(Node head) { 
		this.head = head;
	}


	public void add(Node node) { 
		
		Node current = head; 
		while (current != null) { 
			if (current.next == null) { 
				current.next = node; 
				break; 
			} current = current.next; 
		} 
	}
	
}

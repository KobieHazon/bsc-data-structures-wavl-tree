// Authors: Kobie Hazon and Itzchak Harel


/**
 *
 * WAVLTree
 *
 * An implementation of a WAVL Tree. (Haupler, Sen & Tarajan '15)
 *
 */

public class WAVLTree {

	private WAVLNode root;
	private int size;

	// Names for parameters in functions that get boolean value to know on which
	// side to perform
	private static final boolean RIGHT = true;
	private static final boolean LEFT = !RIGHT;
	// Names for parameters in functions that perform rotations to know how many
	// demotes and promotes to do
	private static final boolean INSERT = true;
	private static final boolean DELETE = !INSERT;

	private WAVLNode min;
	private WAVLNode max;

	enum NodeType { // enum that signifies the options for each node so cases are checked
		ROOT_LEAF, RIGHT_LEAF, LEFT_LEAF, FULL_SONS, RIGHT_UNARY, LEFT_UNARY, EXTERNAL
	}

	enum InsertRebalanceCase { // enum that has the options for the rebalance cases in Insert
		NO_REBALANCE, CASE_1, CASE_2, CASE_3
	}

	enum DeleteRebalanceCase { // enum that has the options for the rebalance cases in Delete
		NO_REBALANCE, CASE_1, CASE_2, CASE_3, CASE_4, RANK1_LEAF
	}

	/**
	 * public WAVLTree()
	 *
	 * constructor for WAVLTree. tree is initialized empty
	 *
	 */
	public WAVLTree() {
		this.root = null;
		this.size = 0;
	}

	/**
	 * public boolean empty()
	 *
	 * returns true if and only if the tree is empty
	 *
	 */
	public boolean empty() {
		return this.root == null;
	}

	/**
	 * public String search(int k)
	 *
	 * returns the info of an item with key k if it exists in the tree otherwise,
	 * returns null
	 */
	public String search(int k) {
		if (this.empty())
			return null;
		WAVLNode found = root.searchSubtree(k);
		return found != null ? found.value : null;
	}

	/**
	 * public String min()
	 *
	 * Returns the info of the item with the smallest key in the tree, or null if
	 * the tree is empty
	 */
	public String min() {
		return this.empty() ? null : min.value;
	}

	/**
	 * public String max()
	 *
	 * Returns the info of the item with the largest key in the tree, or null if the
	 * tree is empty
	 */
	public String max() {
		return this.empty() ? null : max.value;
	}

	/**
	 * public int[] keysToArray()
	 *
	 * Returns a sorted array which contains all keys in the tree, or an empty array
	 * if the tree is empty.
	 */
	public int[] keysToArray() {
		if (this.empty())
			return new int[0];
		int[] arr = new int[this.size];
		root.subtreeKeysToArray(arr, 0);
		return arr;
	}

	/**
	 * public String[] infoToArray()
	 *
	 * Returns an array which contains all info in the tree, sorted by their
	 * respective keys, or an empty array if the tree is empty.
	 */
	public String[] infoToArray() {
		if (this.empty())
			return new String[0];
		String[] arr = new String[this.size];
		root.subtreeValuesToArray(arr, 0);
		return arr;
	}

	/**
	 * public int size()
	 *
	 * Returns the number of nodes in the tree.
	 *
	 */
	public int size() {
		return this.size;
	}

	/**
	 * public WAVLNode getRoot()
	 *
	 * Returns the root WAVL node, or null if the tree is empty
	 *
	 */
	public WAVLNode getRoot() {
		return this.root;
	}

	/**
	 * public int select(int i)
	 *
	 * Returns the value of the i'th smallest key (return -1 if tree is empty)
	 * Example 1: select(1) returns the value of the node with minimal key Example
	 * 2: select(size()) returns the value of the node with maximal key Example 3:
	 * select(2) returns the value 2nd smallest minimal node, i.e the value of the
	 * node minimal node's successor
	 *
	 */
	public String select(int i) {
		if (i <= 0 || i > size()) {
			return null;
		}
		return fingerSearch(i).value;
	}

	/**
	 * private WAVLNode fingerSearch(int i)
	 *
	 * helper function for select that searches for node to continue search uses
	 * overloaded select function
	 */
	private WAVLNode fingerSearch(int i) {
		WAVLNode node = min;
		while (node.getSubtreeSize() < i) {
			node = node.parent;
		}
		return select(node, i - 1);
	}

	/**
	 * private WAVLNode select(WAVLNode node, int i)
	 *
	 * helper recursive function for select like we saw in lesson
	 */
	private WAVLNode select(WAVLNode node, int i) {
		int r = node.left.getSubtreeSize();
		if (i == r) {
			return node;
		} else {
			if (i < r) {
				return select(node.left, i);
			} else {
				return select(node.right, i - r - 1);
			}
		}
	}

	/**
	 * public int insert(int k, String i)
	 *
	 * inserts an item with key k and info i to the WAVL tree. the tree must remain
	 * valid (keep its invariants). returns the number of rebalancing operations, or
	 * 0 if no rebalancing operations were necessary. returns -1 if an item with key
	 * k already exists in the tree.
	 */
	public int insert(int k, String i) {
		WAVLNode currPos = insertBinarySearchTree(k, i);
		if (currPos != null)
		{
			this.size++;
			return RebalanceInsert(currPos.parent);
		}
		return -1;
	}

	/**
	 * private WAVLNode insertBinarySearchTree(int k, String i)
	 *
	 * function to insert like a regular BST, updates min, max and returns newNode
	 * so we can start rebalance
	 */
	private WAVLNode insertBinarySearchTree(int k, String i) {
		WAVLNode newNode = new WAVLNode(k, i);
		if (this.empty()) {
			setRoot(newNode);
			this.min = root;
			this.max = root;
		} else {
			if (k < this.min.key)
				this.min = newNode;
			else if (k > this.max.key)
				this.max = newNode;
			WAVLNode posParent = searchPosition(root, k);
			if (posParent == null)
				return null;
			if (newNode.getKey() < posParent.getKey())
				posParent.setSon(newNode, LEFT);
			else
				posParent.setSon(newNode, RIGHT);
			posParent.updateSizeOnPath(1);
		}
		return newNode;
	}

	/**
	 * private WAVLNode searchPosition(WAVLNode node, int key)
	 *
	 * helper function for insertBinarySearchTree that finds insertion position
	 */
	private WAVLNode searchPosition(WAVLNode node, int key) {
		WAVLNode prev = null;
		while (node.isInnerNode()) {
			prev = node;
			if (key == node.key)
				return null;
			else if (key < node.key)
				node = node.getLeft();
			else
				node = node.getRight();
		}
		return prev;
	}

	/**
	 * public int RebalanceInsert(WAVLNode currNode)
	 *
	 * recursive function that does insert rebalances according to what we learned
	 * in lesson and the case where in case is checked and returned by
	 * checkInsertRebCase(currNode)
	 */
	public int RebalanceInsert(WAVLNode currNode) {
		switch (checkInsertRebCase(currNode)) {
		case NO_REBALANCE:
			return 0;
		case CASE_1:
			return currNode.promote() + RebalanceInsert(currNode.parent);
		case CASE_2:
			if (currNode.left.getRankDiff() == 0) {
				if (isRoot(currNode))
					setRoot(currNode.left);
				return currNode.RotateRight(INSERT) + RebalanceInsert(currNode.parent);
			}
			if (isRoot(currNode))
				setRoot(currNode.right);
			return currNode.RotateLeft(INSERT) + RebalanceInsert(currNode.parent);
		case CASE_3:
			if (currNode.left.getRankDiff() == 0) {
				if (isRoot(currNode))
					setRoot(currNode.left.right);
				return currNode.DoubleRotateLeft(INSERT) + RebalanceInsert(currNode.parent);
			}
			if (isRoot(currNode))
				setRoot(currNode.right.left);
			return currNode.DoubleRotateRight(INSERT) + RebalanceInsert(currNode.parent);
		}
		return 0;
	}

	/**
	 * public InsertRebalanceCase checkInsertRebCase(WAVLNode parent)
	 *
	 * helper function that checks if rebalance is needed for node parent and if it
	 * is then it returns the case
	 */
	public InsertRebalanceCase checkInsertRebCase(WAVLNode parent) {
		if (parent == null)
			return InsertRebalanceCase.NO_REBALANCE;
		if ((parent.left.getRankDiff() == 1 && parent.right.getRankDiff() == 0)
				|| (parent.left.getRankDiff() == 0 && parent.right.getRankDiff() == 1))
			return InsertRebalanceCase.CASE_1;
		if (parent.left.getRankDiff() == 0) {
			if (parent.left.left.getRankDiff() == 2)
				return InsertRebalanceCase.CASE_3;
			return InsertRebalanceCase.CASE_2;
		}
		if (parent.right.getRankDiff() == 0) {
			if (parent.right.right.getRankDiff() == 2)
				return InsertRebalanceCase.CASE_3;
			return InsertRebalanceCase.CASE_2;
		}
		return InsertRebalanceCase.NO_REBALANCE;
	}

	/**
	 * public int delete(int k)
	 *
	 * deletes an item with key k from the binary tree, if it is there; the tree
	 * must remain valid (keep its invariants). returns the number of rebalancing
	 * operations, or 0 if no rebalancing operations were needed. returns -1 if an
	 * item with key k was not found in the tree.
	 */
	public int delete(int k) {
		if (this.empty())
			return -1;
		WAVLNode deletedNode = root.searchSubtree(k);
		if (deletedNode == null)
			return -1;
		this.size--;
		if (this.max == this.min) {
			this.max = null;
			this.min = null;
		} else {
			if (deletedNode == this.max)
				this.max = Predecessor(deletedNode);
			else if (deletedNode == this.min) {
				this.min = Successor(deletedNode);
			}
		}
		return RebalanceDelete(DeleteBinarySearchTree(deletedNode));
	}

	/**
	 * public WAVLNode DeleteBinarySearchTree(WAVLNode toDelete)
	 *
	 * performs delete for regularBinarySearchTree and returns the node to start
	 * rebalancing from The returned node for rebalance is currPos and if the node
	 * to delete has full sons then it is recursive
	 */
	public WAVLNode DeleteBinarySearchTree(WAVLNode toDelete) {
		WAVLNode currPos = null;
		switch (toDelete.getType()) {
		case ROOT_LEAF:
			this.root = null;
			break;
		case LEFT_LEAF:
			toDelete.parent.setSon(new WAVLNode(), LEFT);
			currPos = toDelete.parent;
			break;
		case RIGHT_LEAF:
			toDelete.parent.setSon(new WAVLNode(), RIGHT);
			currPos = toDelete.parent;
			break;
		case LEFT_UNARY:
			if (!isRoot(toDelete)) {
				if (toDelete == toDelete.parent.left) {
					toDelete.parent.setSon(toDelete.left, LEFT);
				} else {
					toDelete.parent.setSon(toDelete.left, RIGHT);
				}
				if (toDelete.left.isInnerNode())
					toDelete.left.rank = toDelete.rank;
			} else {
				setRoot(toDelete.left);
			}
			currPos = toDelete.left;
			break;
		case RIGHT_UNARY:
			if (!isRoot(toDelete)) {
				if (toDelete.isSonSide(RIGHT)) {
					toDelete.parent.setSon(toDelete.right, RIGHT);
				} else {
					toDelete.parent.setSon(toDelete.right, LEFT);
				}
				if (toDelete.right.isInnerNode())
					toDelete.right.rank = toDelete.rank;
			} else {
				setRoot(toDelete.right);
			}
			currPos = toDelete.right;
			break;
		case FULL_SONS:
			WAVLNode deletedSuccessor = Successor(toDelete);
			if (!isRoot(toDelete)) {
				if (toDelete.isSonSide(RIGHT)) {
					toDelete.parent.right = deletedSuccessor;
					currPos = DeleteBinarySearchTree(deletedSuccessor);
					deletedSuccessor.rank = toDelete.rank;
					deletedSuccessor.setSon(toDelete.right, RIGHT);
					deletedSuccessor.setSon(toDelete.left, LEFT);
					currPos = currPos == null ? currPos = deletedSuccessor.parent : currPos;
					deletedSuccessor.parent = toDelete.parent;

				} else {
					toDelete.parent.left = deletedSuccessor;
					currPos = DeleteBinarySearchTree(deletedSuccessor);
					deletedSuccessor.rank = toDelete.rank;
					deletedSuccessor.setSon(toDelete.right, RIGHT);
					deletedSuccessor.setSon(toDelete.left, LEFT);
					currPos = currPos == null ? currPos = deletedSuccessor.parent : currPos;
					deletedSuccessor.parent = toDelete.parent;
				}
			} else {
				currPos = DeleteBinarySearchTree(deletedSuccessor);
				deletedSuccessor.rank = toDelete.rank;
				deletedSuccessor.setSon(toDelete.right, RIGHT);
				deletedSuccessor.setSon(toDelete.left, LEFT);
				currPos = currPos == null ? currPos = deletedSuccessor.parent : currPos;
				setRoot(deletedSuccessor);
			}
			if (currPos == toDelete)
				currPos = deletedSuccessor;
			break;
		default:
			break;
		}
		if (!toDelete.isRoot())
			toDelete.parent.updateSizeOnPath(-1);
		return currPos;
	}

	/**
	 * public int RebalanceDelete(WAVLNode currNode)
	 *
	 * rebalances node currNode according to cases learned in class and is recursive
	 * if needed (calls with currNode.parent) the rebalance case is returned by
	 * checkDeleteRebCase(currNode)
	 */
	public int RebalanceDelete(WAVLNode currNode) {
		if (currNode == null || !currNode.isInnerNode())
			return 0;
		switch (checkDeleteRebCase(currNode)) {
		case NO_REBALANCE:
			return 0;
		case CASE_1:
			return currNode.demote() + RebalanceDelete(currNode.parent);
		case CASE_2:
			if (currNode.right.getRankDiff() == 1)
				return currNode.demote() + currNode.right.demote() + RebalanceDelete(currNode.parent);
			else
				return currNode.demote() + currNode.left.demote() + RebalanceDelete(currNode.parent);
		case CASE_3:
			if (currNode.right.getRankDiff() == 1) {
				if (isRoot(currNode))
					setRoot(currNode.right);
				return currNode.RotateLeft(DELETE) + RebalanceDelete(currNode) + RebalanceDelete(currNode.parent);
			} else {
				if (isRoot(currNode))
					setRoot(currNode.left);
				return currNode.RotateRight(DELETE) + RebalanceDelete(currNode) + RebalanceDelete(currNode.parent);
			}
		case CASE_4:
			if (currNode.right.getRankDiff() == 1) {
				if (isRoot(currNode))
					setRoot(currNode.right.left);
				return currNode.DoubleRotateRight(DELETE) + RebalanceDelete(currNode.parent);
			} else {
				if (isRoot(currNode))
					setRoot(currNode.left.right);
				return currNode.DoubleRotateLeft(DELETE) + RebalanceDelete(currNode.parent);
			}
		case RANK1_LEAF:
			return currNode.demote() + RebalanceDelete(currNode.parent);
		}
		return 0;
	}

	/**
	 * public DeleteRebalanceCase checkDeleteRebCase(WAVLNode z)
	 *
	 * returns the rebalance case (or no_rebalance) according to presentation from
	 * class
	 */
	public DeleteRebalanceCase checkDeleteRebCase(WAVLNode z) {
		if ((z.getType() == NodeType.LEFT_LEAF || z.getType() == NodeType.RIGHT_LEAF) && z.rank == 1)
			return DeleteRebalanceCase.RANK1_LEAF;
		if ((z.left.getRankDiff() == 3 && z.right.getRankDiff() == 2)
				|| (z.left.getRankDiff() == 2 && z.right.getRankDiff() == 3))
			return DeleteRebalanceCase.CASE_1;
		if ((z.left.getRankDiff() == 3 && z.right.getRankDiff() == 1)
				|| (z.left.getRankDiff() == 1 && z.right.getRankDiff() == 3)) {
			if (z.right.getRankDiff() == 1) {
				WAVLNode y = z.right;
				if (y.right.getRankDiff() == 2 && y.left.getRankDiff() == 2)
					return DeleteRebalanceCase.CASE_2;
				if (y.right.getRankDiff() == 1 && (y.left.getRankDiff() == 1 || y.left.getRankDiff() == 2))
					return DeleteRebalanceCase.CASE_3;
				if (y.right.getRankDiff() == 2 && y.left.getRankDiff() == 1)
					return DeleteRebalanceCase.CASE_4;
			} else {
				WAVLNode y = z.left;
				if (y.right.getRankDiff() == 2 && y.left.getRankDiff() == 2)
					return DeleteRebalanceCase.CASE_2;
				if (y.left.getRankDiff() == 1 && (y.right.getRankDiff() == 1 || y.right.getRankDiff() == 2))
					return DeleteRebalanceCase.CASE_3;
				if (y.left.getRankDiff() == 2 && y.right.getRankDiff() == 1)
					return DeleteRebalanceCase.CASE_4;
			}
		}
		return DeleteRebalanceCase.NO_REBALANCE;
	}

	/**
	 * public WAVLNode Successor(WAVLNode x)
	 *
	 * finds the successor of node x using the psuedo-code from the presentation
	 */
	public WAVLNode Successor(WAVLNode x) {
		if (x.right.isInnerNode())
			return x.right.getSubtreeMin();
		WAVLNode y = x.parent;
		while (y.isInnerNode() && x == y.right) {
			x = y;
			y = x.parent;
		}
		return y;
	}

	/**
	 * public WAVLNode Predecessor(WAVLNode x)
	 *
	 * finds the predecessor of node x symmetricly to Successor function
	 */
	public WAVLNode Predecessor(WAVLNode x) {
		if (x.left.isInnerNode())
			return x.left.getSubtreeMax();
		WAVLNode y = x.parent;
		while (y.isInnerNode() && x == y.left) {
			x = y;
			y = x.parent;
		}
		return y;
	}

	/**
	 * private boolean isRoot(WAVLNode node)
	 *
	 * returns boolean that informs if node is the root
	 */
	private boolean isRoot(WAVLNode node) {
		return node == this.root;
	}

	/**
	 * private void setRoot(WAVLNode node)
	 *
	 * void function to set the root to a new node, and as a result set it's parent
	 * to null
	 */
	private void setRoot(WAVLNode node) {
		this.root = node;
		node.parent = null;
	}

	/**
	 * public class WAVLNode
	 */
	public class WAVLNode {

		private int key;
		private String value;

		private WAVLNode left;
		private WAVLNode parent;
		private WAVLNode right;

		private int subtreeSize;
		private int rank;

		/**
		 * public WAVLNode(int key, String value)
		 *
		 * constructor for an internalNode, thus it accepts a key and value
		 */
		public WAVLNode(int key, String value) {
			this.key = key;
			this.value = value;
			this.rank = 0;
			this.subtreeSize = 1;
			this.left = new WAVLNode();
			this.left.parent = this;
			this.right = new WAVLNode();
			this.right.parent = this;
		}

		/**
		 * public WAVLNode()
		 *
		 * constructor for an externalNode, thus it is initialized with rank 0 and
		 * -Infinity key
		 */
		public WAVLNode() {
			this.rank = -1;
			this.key = Integer.MIN_VALUE;
		}

		public int getRank() { // returns rank
			return this.rank;
		}

		public boolean isRoot() { // returns if node is the root (checks the parent field)
			return this.parent == null;
		}

		/**
		 * public NodeType getType()
		 *
		 * function that returnes this node's type (leaf, unary, full...)
		 */
		public NodeType getType() {
			if (!this.isInnerNode()) {
				return NodeType.EXTERNAL;
			}
			if (!this.left.isInnerNode() && !this.right.isInnerNode()) {
				if (this.isRoot())
					return NodeType.ROOT_LEAF;
				if (this.parent.left == this)
					return NodeType.LEFT_LEAF;
				return NodeType.RIGHT_LEAF;
			}
			if (!this.left.isInnerNode())
				return NodeType.RIGHT_UNARY;
			else if (!this.right.isInnerNode())
				return NodeType.LEFT_UNARY;
			return NodeType.FULL_SONS;
		}

		/**
		 * public WAVLNode searchSubtree(int j)
		 *
		 * helper function so we can recursively search for key (late edit) -
		 * recursiveness was dropped for performance
		 */
		public WAVLNode searchSubtree(int j) {
			WAVLNode node = this;
			while (node.isInnerNode() && node.key != j) {
				if (node.key < j)
					node = node.right;
				else {
					node = node.left;
				}
			}
			return node.isInnerNode() ? node : null;
		}

		/**
		 * public int subtreeKeysToArray(int[] keyArray, int index)
		 *
		 * recursive helper function that fills the keyArray with the tree inorder it
		 * uses the index parameter to check where to insert the key
		 */
		public int subtreeKeysToArray(int[] keyArray, int index) {
			if (this.left.isInnerNode())
				index = left.subtreeKeysToArray(keyArray, index);
			keyArray[index] = this.key;
			index++;
			if (this.right.isInnerNode())
				index = right.subtreeKeysToArray(keyArray, index);
			return index;
		}

		/**
		 * public int subtreeValuesToArray(String[] ValueArray, int index)
		 *
		 * recursive helper function that fills the ValueArray with the tree inorder it
		 * uses the index parameter to check where to insert the value
		 */
		public int subtreeValuesToArray(String[] ValueArray, int index) {
			if (this.left.isInnerNode())
				index = left.subtreeValuesToArray(ValueArray, index);
			ValueArray[index] = this.value;
			index++;
			if (this.right.isInnerNode())
				index = right.subtreeValuesToArray(ValueArray, index);
			return index;
		}

		public WAVLNode getSubtreeMin() { // Helper function to return subtree (of this node) minimum
			WAVLNode node = this;
			while (node.left.isInnerNode())
				node = node.left;
			return node;
		}

		public WAVLNode getSubtreeMax() { // Helper function to return subtree (of this node) maximum
			WAVLNode node = this;
			while (node.right.isInnerNode())
				node = node.right;
			return node;
		}

		public int promote() { // promote (returns 1 for rebalance tracking)
			rank += 1;
			return 1;
		}

		public int demote() { // demote (returns 1 for rebalance tracking)
			rank -= 1;
			return 1;
		}

		/**
		 * public int RotateLeft()
		 *
		 * function that performs RotateLeft for either Insert or Delete (Determined by
		 * Type) variable names were chosen in sync with wavl presentation
		 */
		public int RotateLeft(boolean Type) {
			WAVLNode x = this;
			WAVLNode y = this.right;
			WAVLNode b = y.left;
			y.subtreeSize = x.subtreeSize;
			x.subtreeSize -= 1 + y.right.subtreeSize;
			if (!x.isRoot()) {
				if (x.isSonSide(LEFT))
					x.parent.setSon(y, LEFT);
				else
					x.parent.setSon(y, RIGHT);
			} else
				y.parent = null;
			x.right = b;
			b.parent = x;
			x.parent = y;
			y.left = x;
			return Type == INSERT ? 1 + this.demote() : 1 + this.demote() + y.promote();
		}

		/**
		 * public int RotateRight()
		 *
		 * function that performs RotateRight for either Insert or Delete (Determined by
		 * Type) variable names were chosen in sync with wavl presentation
		 */
		public int RotateRight(boolean Type) {
			WAVLNode y = this;
			WAVLNode x = y.left;
			WAVLNode b = x.right;
			x.subtreeSize = y.subtreeSize;
			y.subtreeSize -= 1 + x.left.subtreeSize;
			if (!y.isRoot()) {
				if (y.isSonSide(LEFT))
					y.parent.setSon(x, LEFT);
				else
					y.parent.setSon(x, RIGHT);
			} else
				x.parent = null;
			y.left = b;
			b.parent = y;
			y.parent = x;
			x.right = y;
			return Type == INSERT ? 1 + this.demote() : 1 + this.demote() + x.promote();
		}

		/**
		 * public int DoubleRotateLeft()
		 *
		 * function that performs DoubleRotateLeft for either Insert or Delete
		 * (Determined by Type) Type determins if demote or promote will be performed
		 */
		public int DoubleRotateLeft(boolean Type) {
			return (this.left.RotateLeft(Type) + this.RotateRight(Type))
					+ (Type == INSERT ? this.parent.promote() : this.demote());
		}

		/**
		 * public int DoubleRotateRight()
		 *
		 * function that performs DoubleRotateRight for either Insert or Delete
		 * (Determined by Type) Type determins if demote or promote will be performed
		 */
		public int DoubleRotateRight(boolean Type) {
			return (this.right.RotateRight(Type) + this.RotateLeft(Type))
					+ (Type == INSERT ? this.parent.promote() : this.demote());
		}

		/**
		 * public int getRankDiff()
		 *
		 * function that returns the rank difference of this node with its parent
		 */
		public int getRankDiff() {
			if (this.isRoot())
				return -10;
			return (Math.abs(this.parent.rank - this.rank));
		}

		/**
		 * public boolean isSonSide(boolean side)
		 *
		 * returns if this node is a left or right node of its parent
		 */
		public boolean isSonSide(boolean side) {
			return side == RIGHT ? this.parent.right == this : this.parent.left == this;
		}

		/**
		 * public void setSon(WAVLNode node, boolean side)
		 *
		 * void function that sets the (side) son of this to node
		 */
		public void setSon(WAVLNode node, boolean side) {
			if (side == RIGHT) {
				this.right = node;
				this.right.parent = this;
			} else {
				this.left = node;
				this.left.parent = this;
			}
		}
		
		/**
		 * private void changeSizeOnPath(int i)
		 *
		 * updates the size on the path from this node to the root by incrementing in i
		 * function is called in insert and delete with 1 and -1 respectively
		 */
		private void updateSizeOnPath(int i) {
			WAVLNode toInc = this;
			while (toInc != null) {
				toInc.subtreeSize += i;
				toInc = toInc.parent;
			}
		}

		// simple helper functions
		public int getKey() {
			return this.key;
		}

		public String getValue() {
			return this.value;
		}

		public WAVLNode getLeft() {
			return this.left;
		}
		
		public int getSubtreeSize() { return this.rank == -1 ? 0 : 1 + this.left.getSubtreeSize() 
		+ this.right.getSubtreeSize();}

		public WAVLNode getRight() {
			return this.right;
		}

		public boolean isInnerNode() {
			return rank != -1;
		}

		
	}
}

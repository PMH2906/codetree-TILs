import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class Main {
	
	static BufferedReader input=new BufferedReader(new InputStreamReader(System.in));
	static StringTokenizer tokens;
	static StringBuilder output=new StringBuilder();
	static int Q, index,INF=1000000000;
	static Map<String, Integer> nameToIndex;
	static Set<Integer> usedValue;
	static int[] values=new int[100001];
	static String[] names=new String[100001];
	static List<SegmentTreeNode> segmentTree=new ArrayList<>();
	
	public static class SegmentTreeNode{
		int left, right, index, count;
		long sum;

		public SegmentTreeNode() {
			super();
			this.left = 0;
			this.right = 0;
			this.index = 0;
			this.count = 0;
			this.sum = 0;
		}
		
	}
	
	public static void main(String[] args) throws IOException {
		
		tokens=new StringTokenizer(input.readLine());
		Q=Integer.parseInt(tokens.nextToken());
		index=0;
		names=new String[100001];
		values=new int[100001];
		segmentTree=new ArrayList<>();
		nameToIndex=new HashMap<String, Integer>();
		usedValue=new HashSet<Integer>();
		
		for(int q=0;q<Q;q++) {
			tokens=new StringTokenizer(input.readLine());
			
			String order=tokens.nextToken();
			if(order.equals("init")) init();
			else if(order.equals("insert")) {
				String name=tokens.nextToken();
				int value=Integer.parseInt(tokens.nextToken());
				int result=insert(name, value);
				output.append(result+"\n");
			}
			else if(order.equals("delete")) {
				String name=tokens.nextToken();
				int result=delete(name);
				output.append(result+"\n");
			}
			else if(order.equals("rank")) {
				int rank=Integer.parseInt(tokens.nextToken());
				String result=rank(rank);
				output.append(result+"\n");
			}
			else if(order.equals("sum")) {
				int sum=Integer.parseInt(tokens.nextToken());
				long result=sum(sum);
				output.append(result+"\n");
			}
		}
		System.out.println(output);

	}

	private static long sum(int sum) {
		
		return querySum(1, 1, INF, 1, sum);
	}

	private static long querySum(int nodeId, int left, int right, int rangeLeft, int rangeRight) {
		// 다음 왼쪽 노드 혹은 오른쪽 노드가 없는 경우가 있으므로 세번째 조건도 추가하기 
		if(rangeLeft>right||rangeRight<left || nodeId == 0) return 0;
		
		if(rangeLeft<=left&&right<=rangeRight) return segmentTree.get(nodeId).sum;
		
		
		
//		int mid = (left + right) / 2;
//	       long result = 0;
//	        // 왼쪽 자식 노드가 존재하면 왼쪽 구간의 합을 구합니다.
//	        if (segmentTree.get(nodeId).left != 0) {
//	            result += querySum(segmentTree.get(nodeId).left, left, mid, rangeLeft, rangeRight);
//	        }
//	        // 오른쪽 자식 노드가 존재하면 오른쪽 구간의 합을 구합니다.
//	        if (segmentTree.get(nodeId).right != 0) {
//	            result += querySum(segmentTree.get(nodeId).right, mid + 1, right, rangeLeft, rangeRight);
//	        }
//	    return result;
	    
		long leftSum=querySum(segmentTree.get(nodeId).left, left, (right+left)/2, rangeLeft, rangeRight);
		long rightSum=querySum(segmentTree.get(nodeId).right, (right+left)/2+1, right, rangeLeft, rangeRight);
		return leftSum+rightSum; 
	}

	private static String rank(int rank) {
		
		if(segmentTree.get(1).count<rank) return "None";
		int rankIndex=queryRank(1, 1, INF, rank);
		String rankName=names[rankIndex];
		return rankName;
	}

	private static int queryRank(int nodeId, int left, int right, int rank) {
		
		if(left==right) {
			return segmentTree.get(nodeId).index;
		}
		
		// rank가 왼쪽 노드 총 갯수보다 작거나 같으면, 왼쪽 노드 탐색 
		if(segmentTree.get(segmentTree.get(nodeId).left).count>=rank) {
			return queryRank(segmentTree.get(nodeId).left, left, (left+right)/2, rank);
		}
		
		// rank가 왼쪽 노드 총 갯수보다 크면, 오른쪽 노드 탐색 
		// rank는 오른쪽 노드 기준으로 갱신해줘야하므로, rank에서 왼쪽 노드의 총 갯수를 빼주기 
		return queryRank(segmentTree.get(nodeId).right, (left+right)/2+1, right, rank-segmentTree.get(segmentTree.get(nodeId).left).count);
	}

	private static int delete(String name) {
		if(!nameToIndex.containsKey(name)) return 0;
		
		int deleteIndex=nameToIndex.get(name);
		nameToIndex.remove(name);
		
		int deleteValue=values[deleteIndex];
		usedValue.remove(deleteValue);
		
		// 세그먼트 트리에서 삭제 
		// sum과 count를 0으로 셋팅하면 해당 값은 0으로 되며, 부모 노드도 0으로 업데이트된 값을 기준으로 업데이트됨  
		update(1,1,INF,deleteValue,index,0,0);
		return deleteValue;
	}

	private static int insert(String name, int value) {
		if(nameToIndex.containsKey(name)||usedValue.contains(value)) return 0;
		
		nameToIndex.put(name,++index);
		usedValue.add(value);
		
		names[index]=name;
		values[index]=value;
		
		update(1,1,INF,value, index, 1, value);
		return 1;
	}

	private static void update(int nodeId, int left, int right, int value, int index, int count, int sum) {
		
		// 넣을 숫자가 범위를 넘어가면 종료  
		if(value<left||right<value) return; 
		
		if(left==right) {
			segmentTree.get(nodeId).index=index;
			segmentTree.get(nodeId).count=count;
			segmentTree.get(nodeId).sum=sum;
			return;
		}
		
		int mid=(left+right)/2;
		
		// 넣을 숫자가 중간값보다 작으면 왼쪽 노드로 움직이기
		// 만약 왼쪽 노드가 없으면 생성 
		if(value<=mid) {
			if(segmentTree.get(nodeId).left==0) {
				// 왼쪽이 가질 노드 번호 
				segmentTree.get(nodeId).left=segmentTree.size();
				// 왼쪽이 가질 노드 추가 
				segmentTree.add(new SegmentTreeNode());
			}
			update(segmentTree.get(nodeId).left,left, mid, value, index, count, sum);
		} else {
			if(segmentTree.get(nodeId).right==0) {
				// 왼쪽이 가질 노드 번호 
				segmentTree.get(nodeId).right=segmentTree.size();
				// 왼쪽이 가질 노드 추가 
				segmentTree.add(new SegmentTreeNode());		
			}
			update(segmentTree.get(nodeId).right,mid+1, right, value, index, count, sum);
		}
		
		// 현재 노드의 count와 sum을 왼쪽과 오른쪽 자식 노드의 count와 sum으로 더해주기 
		int leftCount=segmentTree.get(segmentTree.get(nodeId).left).count;
		int rightCount=segmentTree.get(segmentTree.get(nodeId).right).count;
		long leftSum=segmentTree.get(segmentTree.get(nodeId).left).sum;
		long rightSum=segmentTree.get(segmentTree.get(nodeId).right).sum;
		segmentTree.get(nodeId).count=leftCount+rightCount;
		segmentTree.get(nodeId).sum=leftSum+rightSum;
	}

	private static void init() {
		segmentTree.clear();
		segmentTree.add(new SegmentTreeNode()); // 0 인덱스는 사용 X
		segmentTree.add(new SegmentTreeNode()); // 세그먼트 트리의 첫번째 노드 초기화 
		
		nameToIndex.clear();
		usedValue.clear();
		
		values=new int[100001];
		names=new String[100001];
		index=0;
	}

}

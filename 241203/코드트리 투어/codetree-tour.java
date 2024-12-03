import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.StringTokenizer;

public class Main {
	static BufferedReader input=new BufferedReader(new InputStreamReader(System.in));
	static StringBuilder output=new StringBuilder();
	static StringTokenizer tokens;
	static Map<Integer, Product> products;
	static PriorityQueue<Product> pq; // 최적의 여행 상품 판매를 바로 뽑기위해 우선순위 큐 생성 
	static int[] minDist;
	static int Q, N, M,start;
	static List<Node>[] nodes;
	
	public static class Node implements Comparable<Node>{
		int to, weight;

		public Node(int to, int weight) {
			super();
			this.to = to;
			this.weight = weight;
		}

		@Override
		public int compareTo(Node o) {
			return Integer.compare(this.weight, o.weight);
		}
		
	}
	
	public static class Product implements Comparable<Product>{
		int id, revenue, dest,value;

		public Product(int id, int revenue, int dest) {
			super();
			this.id=id;
			this.revenue = revenue;
			this.dest = dest;
		}
		public Product(int id, int revenue, int dest, int value) {
			super();
			this.id=id;
			this.revenue = revenue;
			this.dest = dest;
			this.value=value;
		}
		@Override
		public int compareTo(Product o) {
			if(this.value==o.value) {
				return Integer.compare(this.id, o.id);
			}
			return Integer.compare(this.value, o.value)*-1;
		}
	}
	
	public static void main(String[] args) throws IOException {
		
		tokens=new StringTokenizer(input.readLine());
		Q=Integer.parseInt(tokens.nextToken());
		
		for(int q=0;q<Q;q++) {
			tokens=new StringTokenizer(input.readLine());
			int order=Integer.parseInt(tokens.nextToken());
			
			if(order==100) {
				N=Integer.parseInt(tokens.nextToken());
				M=Integer.parseInt(tokens.nextToken());
				start=0;
				minDist=new int[N];
				nodes=new ArrayList[N];
				products=new HashMap<>();
				pq=new PriorityQueue<>();
				for(int n=0;n<N;n++) {
					nodes[n]=new ArrayList<>();
				}
				for(int m=0;m<M;m++) {
					int v=Integer.parseInt(tokens.nextToken());
					int u=Integer.parseInt(tokens.nextToken());
					int w=Integer.parseInt(tokens.nextToken());
					
					nodes[v].add(new Node(u,w));
					nodes[u].add(new Node(v,w));
				}
				dijkstra(start);
			} 
			else if(order==200) {
				int id=Integer.parseInt(tokens.nextToken());
				int revenue=Integer.parseInt(tokens.nextToken());
				int dest=Integer.parseInt(tokens.nextToken());
				int cost=minDist[dest];
				
				products.put(id, new Product(id, revenue, dest));
				
				// 새로운 상품 생성할 때마다 pq 추가 -> O(log n)
				if(revenue>=cost) pq.add(new Product(id, revenue, dest, revenue-cost));
			} 
			else if(order==300) {
				int id=Integer.parseInt(tokens.nextToken());
				products.remove(id);
			} 
			
			// 최적의 여행 상품 판매는 최대 30,000번 주어지므로 pq를 이용해 한 번에 빼기 
			else if(order==400) {
				output.append(sale()+"\n");
			} 
			
			// 출발지 변경은 최대 15번 이므로 다익스트라와 pq 리셋을 진행해도 됨 
			else if(order==500) {
				start=Integer.parseInt(tokens.nextToken());
				dijkstra(start);
				reset();
			}
		}
		System.out.println(output);
	}

	// 최적의 여행 상품 판매를 위한 pq 리셋 
	private static void reset() {
		pq=new PriorityQueue<>();
		
		for(Integer id:products.keySet()) {
			int revenue=products.get(id).revenue;
			int dest=products.get(id).dest;
			int cost=minDist[dest];
			
			if(revenue>=cost) pq.add(new Product(id, revenue, dest, revenue-cost));
		}
	}

	private static int sale() {
		
		while(pq.size()>0) {
			// 최적의 여행 상품 판매 -> O(log n)
			Product selectProduct=pq.poll();
			
			// 제거된 상품 id는 패스 
			if(!products.containsKey(selectProduct.id)) continue;
			
			products.remove(selectProduct.id);
			
			return selectProduct.id;
		}
		return -1;
	}

	// 다익스트라 시간 복잡도 EO(logV)
	private static void dijkstra(int start) {
		minDist=new int[N];
		Arrays.fill(minDist, Integer.MAX_VALUE);
		minDist[start]=0;
		
		PriorityQueue<Node> pq=new PriorityQueue<>();
		pq.add(new Node(start, 0));
		
		while(pq.size()>0) {
			Node now=pq.poll();
			
			if(minDist[now.to]<now.weight) continue;
			
			for(Node next : nodes[now.to]) {
				int weight=minDist[now.to]+next.weight;
				if(minDist[next.to]>weight) {
					minDist[next.to]=weight;
					pq.add(new Node(next.to, minDist[next.to]));
				}
			}
		}
	}

}

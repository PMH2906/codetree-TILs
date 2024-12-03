import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.StringTokenizer;

public class Main {
	static BufferedReader input=new BufferedReader(new InputStreamReader(System.in));
	static StringBuilder output=new StringBuilder();
	static StringTokenizer tokens;
	static Map<Integer, Product> products;
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
	
	public static class Product {
		int revenue, dest;

		public Product(int revenue, int dest) {
			super();
			this.revenue = revenue;
			this.dest = dest;
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
				
				products.put(id, new Product(revenue, dest));
			} 
			else if(order==300) {
				int id=Integer.parseInt(tokens.nextToken());
				products.remove(id);
			} 
			else if(order==400) {
				output.append(sale()+"\n");
			} 
			else if(order==500) {
				int s=Integer.parseInt(tokens.nextToken());
				dijkstra(s);
			}
		}
		System.out.println(output);
	}

	private static Object sale() {
		int maxValue=Integer.MIN_VALUE;
		int maxId=-1;
		for(Integer id:products.keySet()) {
			int revenue=products.get(id).revenue;
			int dest=products.get(id).dest;
			int cost=minDist[dest];
			
			if(cost==Integer.MAX_VALUE) continue;
			if(revenue<cost) continue;
			if(maxValue<revenue-cost) {
				maxValue=revenue-cost;
				maxId=id;
			}
		}
		
		if(maxId==-1) return -1;
		products.remove(maxId);
		return maxId;
	}

	private static void dijkstra(int start) {
		minDist=new int[N+1];
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

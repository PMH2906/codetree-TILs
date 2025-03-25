'''
2024년 하반기 오후 1번
메두사와 전사들

푼 시간 2025-03-23
어느정도 설계까지 1시간...
'''
# 되게 복잡하긴 하다
# 근데 못풀건 없다 시간이 4시간 이니


## new_field로 갈아야 함

from collections import deque

N, M = map(int, input().split())
sr, sc, er, ec = map(int, input().split())

knight_list = ["DUMMY"]
coords = list(map(int,input().split()))
for i in range(M):
    knight_list.append([coords[i*2], coords[i*2+1], 1, i+1]) # r, c, state, idx ,, state 1: live, -1 :stun, -2: dead

field = [list(map(int,input().split())) for _ in range(N)]
knight_field = [[0]*N for _ in range(N)]
for knight in knight_list[1:]:
    r,c, state, idx = knight
    knight_field[r][c] += 1

def print_pretty_table(table):
    N = len(table)
    col_widths = [max(len(str(int(table[r][c]))) for r in range(N)) for c in range(N)]
    separator = "+" + "-".join("-"*(col_widths[c] + 2) for c in range(N)) + "+"
    
    print(separator)
    for r in range(N):
        row_str = " | ".join(f"{table[r][c]:>{col_widths[c]}}" for c in range(N))
        print(f"| {row_str} |")
        print(separator)
        
            
def is_inrange(r,c):
    return (0<= r < N and 0 <= c < N)

def distance(r1, c1, r2, c2): # 맨허튼 거리
    return (abs(r1-r2) + abs(c1-c2))

def medusa_bfs(sr, sc, er, ec):    
    visited = [[-1]*N for _ in range(N)]
    queue = deque([[sr,sc]])
    visited[sr][sc] = (sr,sc)
    track = []
    
    while queue:
        cr, cc = queue.popleft()
        for move in range(4):
            nr = cr + dr[move]
            nc = cc + dc[move]
            
            if is_inrange(nr,nc) and visited[nr][nc] == -1 and field[nr][nc] != 1:
                queue.append([nr,nc])
                visited[nr][nc] = (cr,cc)
                
                if nr == er and nc == ec:
                    # track 만들기
                    currentR, currentC = cr, cc
                    while currentR != sr or currentC != sc:
                        track.append([currentR, currentC])
                        currentR, currentC = visited[currentR][currentC]
                        
                    return track
    
    # 빠져나오면 unreachable 한 것
    return -1

def medusa_view(side, mr, mc):
    victims = []
    visited = [[False]*N for _ in range(N)]
    block_visited = [[False]*N for _ in range(N)]
    queue = deque([[mr,mc]])
    count = 0 
    while queue:
        cr, cc= queue.popleft()
        for k in range(-1,2):
            nr = cr + dr[side] + dc[side]*k
            nc = cc + dc[side] + dr[side]*k
            
            if is_inrange(nr,nc) and knight_field[nr][nc] >= 1 and not visited[nr][nc] and not block_visited[nr][nc]:
                visited[nr][nc] = True
                block_visited = block_view(nr,nc, block_visited, dr[side], dc[side], mr, mc)
                victims.append([nr,nc])
                count += knight_field[nr][nc]
                
            elif is_inrange(nr,nc) and knight_field[nr][nc] == 0 and not visited[nr][nc] and not block_visited[nr][nc]: # 아무도 없다면
                queue.append([nr,nc])
                visited[nr][nc] = True

    return victims, visited, count

def block_view(r,c, visited, dr, dc, mr, mc):
    '''
    dr, dc 는 medusa_view에서 이미 dr[side], dc[side]로 입력되어서 1,-1,0 중 하나인 int 값임
    '''
    assert dr in [1,-1,0] and dc in [1,-1,0], "block_view error"
    
    # tmp_visited = [[visited[r][c] for c in range(N)] for r in range(N)]
    
    tmp_dr = (r-mr) // abs(r-mr) if abs(r-mr) !=0 else 0
    tmp_dc = (c-mc) // abs(c-mc) if abs(c-mc) !=0 else 0
    
    queue =deque([[r,c]])
    while queue:
        cr, cc = queue.popleft()
        for k in range(0,2):
            nr = cr + dr + abs(dc)*tmp_dr*k
            nc = cc + dc + abs(dr)*tmp_dc*k
            
            if is_inrange(nr,nc) and not visited[nr][nc]:
                queue.append([nr,nc])
                visited[nr][nc] = True
    
    return visited

def knight_move(r, c, medusa_visited, mr, mc):
    visited = [[False]*N for _ in range(N)]
    queue = deque([[r,c]])
    visited[r][c] = True
    
    cr ,cc = queue.popleft()
    dist_before = distance(cr,cc, mr,mc)
    for move in range(4):
        nr = cr + dr[move]
        nc = cc + dc[move]
        
        if is_inrange(nr,nc) and not medusa_visited[nr][nc] and distance(nr, nc, mr,mc) < dist_before and not visited[nr][nc]:
            queue.append([nr,nc])
            visited[nr][nc] = True
            
            if nr == mr and nc == mc:
                return nr, nc
            break
    
    if not queue:
        return r,c
    
    cr ,cc = queue.popleft()
    dist_before = distance(cr,cc, mr,mc)
    for move in range(4):
        nr = cr + dr[(move+2)%4]
        nc = cc + dc[(move+2)%4]
        
        if is_inrange(nr,nc) and not medusa_visited[nr][nc] and distance(nr, nc, mr,mc) < dist_before and not visited[nr][nc]:
            queue.append([nr,nc])
            visited[nr][nc] = True
            
            if nr == mr and nc == mc:
                return nr,nc
            break
    
    if not queue:
        return cr, cc
    
    return nr, nc

dr = [-1 ,1, 0, 0]
dc = [0, 0, -1, 1]

# print("init_Field")
# print_pretty_table(knight_field)
def main():
    turn = 0
    global knight_field

    output = medusa_bfs(sr,sc,er,ec)
    if output == -1:
        print(-1)
        return
    # print(output)
    while output:
        new_knight_field = [[0]*N for _ in range(N)] # 숫자여서 복사 해도될 듯?

        distance_count =0
        stun_count = 0
        attack_count = 0
        mr, mc = output[-1] # 메두사의 위치

        victim_length = -1
        best_victims, best_visited = [], []
        for side in range(4):
            victims ,visited, count = medusa_view(side, mr, mc)
            if count > victim_length:
                victim_length = count
                best_victims = victims
                best_visited = visited
        
        victims, visited = best_victims, best_visited
        
        if knight_field[mr][mc] != 0: # 메두사가 움직여서 부딛힌 애 삭제
            knight_field[mr][mc] = 0 
            new_knight_field[mr][mc] = 0
            
        # print("medusa visited")
        # print_pretty_table(visited)
            
        # print(f"vicitms {victims}")

        # print_pretty_table(knight_field)
        # print_pretty_table(visited)
        # print(knight_list[4])
        ## knight move
        
        for i in range(N):
            for j in range(N):
                if knight_field[i][j] == 0:
                    continue
                
                if [i,j] in victims:
                    new_knight_field[i][j] = knight_field[i][j]
                    stun_count += knight_field[i][j]
                    continue
                
                output_move = knight_move(i,j, visited, mr, mc)
                nr, nc = output_move
                # print(nr, nc)
                if nr == mr and nc == mc:
                    attack_count += knight_field[i][j]
                
                else:
                    new_knight_field[nr][nc] += knight_field[i][j]
                    
                
                distance_count += (abs(nr-i) + abs(nc - j))*knight_field[i][j]
           
        knight_field = new_knight_field
        
        output.pop()
        print(distance_count, stun_count, attack_count)
        # turn += 1
        
        # if turn ==2:
        #     return
        
    print(0)
main()



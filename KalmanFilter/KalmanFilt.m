function [Xold, Pold] = KalmanFilt()
Zk = [1,2,3,4,5,6,7,8,9,10];%Measurements from the encoder (p x 1)
Xold = zeros(length(Zk),1);%Predicted State vector at time-step k (n x 1)
Xold(1) = 1;
Pold = zeros(length(Zk),1);%Predicted estimate covariance
Pold(1) = 5;
Unew = 0; % Control input vector at time-step k (m) 
Hk = 1;% Relates z to x (p x n)
Bk = 1; % Control input to state vector matrix (n x m)
Fk = 1; % Discrete time transition matrix that relates xk-1 to xk (n x n)
Xcov = 1;
Zcov = 1;
for k = 1:length(Zk)
    [Xnew, Pnew] = KalmanFilterMichael(Zk(k), Xold(k), Pold(k), Xcov, Zcov, Unew, Hk, Bk, Fk)
    Xold(k+1) = Xnew
    Pold(k+1) = Pnew
end
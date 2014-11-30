function [Xnew, Pnew] = KalmanFilterMichael(Zk, Xold, Pold, Xcov, Zcov, Unew, Hk, Bk, Fk)
%% Gather inputs
Qk = Xcov; % State covariance
Rk = Zcov; % Observation covariance
Uk = Unew;
%% Predict Stage
Xpred = Fk * Xold + Bk * Uk; % Internal Dynamics.
Ppred = Fk * Pold * Fk' + Qk; % Prediction of estimate covariance
%% Support Calculations (part of the update stage)
Yk = Zk - Hk * Xpred; % Find residuals (error)
Sk = Hk * Ppred * Hk' + Rk; % Innovation covariance
Kk = Ppred * Hk' * Sk^-1; % Calc Kalman Gain
%% Correction stage (final part of the update stage)
Xnew = Xpred + Kk * Yk; % Updated State estimate
Pnew = (eye(length(Hk)) - Kk * Hk) * Ppred; % updated estimate covariance
end
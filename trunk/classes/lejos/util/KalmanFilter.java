package lejos.util;

public class KalmanFilter {
  private Matrix a, b, c, i, q, r, at, ct;
  private Matrix mu, sigma;
  
  public KalmanFilter(Matrix a, Matrix b, Matrix c, Matrix q, Matrix r) {
    this.a = a;
    this.b = b;
    this.c = c;
    this.q = q;
    this.r = r;
    this.at = a.transpose();
    this.ct = c.transpose();
  }
  
  public void setState(Matrix mean, Matrix covariance) {
    this.mu = mean;
    this.sigma = covariance;
    int n = mu.getColumnDimension();
    this.i = Matrix.identity(n, n);
  }
  
  public void update(Matrix control, Matrix measurement) throws Exception {
    // Control update step 1: calculate the predicted mean
	Matrix muBar = a.times(mu).plus(b.times(control));
    
    // Control update step 2: calculate the predicted covariance
    Matrix sigmaBar = a.times(sigma).times(at).plus(r);
   
    // Calculate the Kalman Gain   
    Matrix gain = sigmaBar.times(ct).times(c.times(sigmaBar).times(ct).plus(q).inverse());
    
    // Measurement update: calculate the new mean
    mu = muBar.plus(gain.times(measurement.minus(ct.times(muBar))));
    
    // Calculate the new covariance
    sigma = i.minus(gain.times(ct)).times(sigmaBar);
  }
  
  public Matrix getMean() {
    return mu;
  }
  
  public Matrix getCovariance() {
    return sigma;
  }
}

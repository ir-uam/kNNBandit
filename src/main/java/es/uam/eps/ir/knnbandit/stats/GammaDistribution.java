/* 
 * Copyright (C) 2019 Information Retrieval Group at Universidad Autónoma
 * de Madrid, http://ir.ii.uam.es.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0.
 * 
 */
package es.uam.eps.ir.knnbandit.stats;

import java.util.Random;

/**
 * Gamma distribution. 
 * @author Javier Sanz-Cruzado (javier.sanz-cruzado@uam.es)
 * @author Pablo Castells (pablo.castells@uam.es)
 */
public class GammaDistribution implements UnivariateStatisticalDistribution
{
    /**
     * Shape parameter.
     */
    private double shape;
    /**
     * Scale parameter.
     */
    private double scale;
    /**
     * Random number generator.
     */
    private final Random rng;

    /**
     * Constructor.
     * @param shape Initial value of the shape parameter.
     * @param scale Initial value of the scale parameter.
     */
    public GammaDistribution(double shape, double scale)
    {
        this.shape = shape;
        this.scale = scale;
        this.rng = new Random();
    }

    @Override
    public void update(Double... values)
    {
        if(values.length == 2)
        {
            this.update(values[0], values[1]);
        }
    }

    /**
     * Updates the distribution changing both values.
     * @param shape Shape parameter.
     * @param scale Scale parameter.
     */
    public void update(double shape, double scale)
    {
        this.shape = shape;
        this.scale = scale;
    }

    @Override
    public void update(double value, int i)
    {
        switch(i)
        {
            case 0:
                this.updateShape(value);
                break;
            case 1:
                this.updateScale(value);
                break;
            default:
                return;
        }
    }

    /**
     * Updates the scale parameter.
     * @param value The new value.
     */
    private void updateScale(double value)
    {
        this.scale = value;
    }

    /**
     * Updates the shape parameter.
     * @param value The new value.
     */
    private void updateShape(double value)
    {
        this.shape = shape;
    }

    @Override
    public double mean() {
        return shape*scale;
    }

    @Override
    public double getParameter(int i)
    {
        switch(i)
        {
            case 0:
                return this.shape;
            case 1:
                return this.scale;
            default:
                return Double.NaN;
        }
    }

    /**
     * This implementation was adapted from https://github.com/gesiscss/promoss.
     */
    @Override
    public double sample()
    {
        if (shape <= 0) return 0; 
        else if (shape == 1) 
            return -Math.log(rng.nextDouble());
        else if (shape < 1) { 
            double c = 1.0 / shape;
            double d = 1.0 / (1 - shape);
            while (true) {
                double x = Math.pow(rng.nextDouble(), c);
                double y = x + Math.pow(rng.nextDouble(), d);
                if (y <= 1) return -Math.log(rng.nextDouble()) * x/y;
            }
        } else {
            double b = shape - 1;
            double c = 3 * shape - 0.75;
            while (true) {
                double u = rng.nextDouble();
                double v = rng.nextDouble();
                double w = u * (1 - u);
                double y = Math.sqrt(c/w) * (u - 0.5);
                double x = b + y;
                if (x >= 0) {
                    double z = 64 * w * w * w * v * v;
                    if ((z <= (1 - 2 * y * y/x))
                            || (Math.log(z) <= 2 * (b * Math.log(x/b) - y)))
                        return x;
                }
            }
        }
    }
}

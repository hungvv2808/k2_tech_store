'use strict';
var gulp = require('gulp');
var sass = require('gulp-sass');
// css min
var cssmin = require('gulp-cssmin');
var rename = require('gulp-rename');
// js min
var jsmin = require('gulp-jsmin');
var rename = require('gulp-rename');

// task for sass
gulp.task('sass', function() {
    return gulp.src('./resources/sass/*.scss')
        .pipe(sass().on('error', sass.logError))
        .pipe(gulp.dest('./resources/css'));
});

// minify css file
gulp.task('cssmin', function(done) {
    gulp.src('./resources/css/*.css')
        .pipe(cssmin())
        .pipe(rename({ suffix: '.min' }))
        .pipe(gulp.dest('./resources/css'));
    done();
});

// minify js file
gulp.task('jsmin', function(done) {
    gulp.src('./resources/js/*.js')
        .pipe(jsmin())
        .pipe(rename({ suffix: '.min' }))
        .pipe(gulp.dest('./resources/js'));
    done();
});

gulp.task('run', gulp.series(['sass', 'cssmin', 'jsmin']));

// main
gulp.task('default', gulp.series(['run']));
/**
 * 此文件用于mvn package以后，替换jsp文件中的js和css路径
 * @type {*|Gulp}
 */
const gulp = require('gulp');
const uglify = require('gulp-uglify');
const minifyCss = require('gulp-minify-css');                     //- 压缩CSS为一行；
const rev = require('gulp-rev');                                  //- 对文件名加MD5后缀
const revCollector = require('gulp-rev-collector');
const runSequence = require('run-sequence');//控制task顺序  
const minimist = require('minimist');
const util = require('gulp-util');

//
var defaultOptions = {
    string: 'profile',
    default: {profile: 'dev'}
};
var options = minimist(process.argv.slice(2), defaultOptions);

gulp.task('build-js', function () {
    return gulp.src(['src/main/webapp/static/**/*.js'])
        .pipe(uglify())
        .on('error', function (err) { util.log(util.colors.red('[Error]'), err.toString()); })
        .pipe(rev())
        .pipe(gulp.dest('target/box-web-person-' + options.profile + '/static'))
        .pipe(rev.manifest())
        .pipe(gulp.dest('target/rev/js'));
});

gulp.task('build-css', function () {
    return gulp.src(['src/main/webapp/static/**/*.css'])
        .pipe(minifyCss())
        .pipe(rev())
        .pipe(gulp.dest('target/box-web-person-' + options.profile + '/static'))
        .pipe(rev.manifest())
        .pipe(gulp.dest('target/rev/css'));
});

gulp.task('build-others', function () {
    return gulp.src(['src/main/webapp/static/**/*', '!src/main/webapp/static/**/*.js', '!src/main/webapp/static/**/*.css', 'src/deploy/' + options.profile + '/webapp/static/**/*'])
        .pipe(gulp.dest('target/box-web-person-' + options.profile + '/static'))
});

/*
gulp.task('build-images', function () {
    return gulp.src(['src/main/webapp/!**!/!*.png', 'src/main/webapp/!**!/!*.jpg', 'src/main/webapp/!**!/!*.gif'])
        .pipe(rev())
        .pipe(gulp.dest('dist'))
        .pipe(rev.manifest())
        .pipe(gulp.dest('dist/rev/images'));
});
*/

gulp.task('build-jsp-js', function () {
    return gulp.src(['target/rev/js/*.json', 'target/box-web-person-' + options.profile + '/WEB-INF/views/**/*.jsp'], {base: 'target/box-web-person-' + options.profile + '/WEB-INF/views'})
        .pipe(revCollector({
            replaceReved: true,
            dirReplacements: {}
        }))
        .pipe(gulp.dest('target/box-web-person-' + options.profile + '/WEB-INF/views'));
});

gulp.task('build-jsp-css', function () {
    return gulp.src(['target/rev/css/*.json', 'target/box-web-person-' + options.profile + '/WEB-INF/views/**/*.jsp'], {base: 'target/box-web-person-' + options.profile + '/WEB-INF/views'})
        .pipe(revCollector({
            replaceReved: true,
            dirReplacements: {}
        }))
        .pipe(gulp.dest('target/box-web-person-' + options.profile + '/WEB-INF/views'));
});

gulp.task('default', function (callback) {
    runSequence(
        'build-js',
        'build-css',
        'build-others',
        'build-jsp-js',
        'build-jsp-css',
        callback);
});
//gulp.task('watch', function () {  
//    gulp.watch('**', ['default']);  
//}); 
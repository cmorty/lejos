; Auto-generated. Do not edit!


(cl:in-package nxt_lejos_msgs-msg)


;//! \htmlinclude DNSCommand.msg.html

(cl:defclass <DNSCommand> (roslisp-msg-protocol:ros-message)
  ((type
    :reader type
    :initarg :type
    :type cl:string
    :initform "")
   (value
    :reader value
    :initarg :value
    :type cl:float
    :initform 0.0))
)

(cl:defclass DNSCommand (<DNSCommand>)
  ())

(cl:defmethod cl:initialize-instance :after ((m <DNSCommand>) cl:&rest args)
  (cl:declare (cl:ignorable args))
  (cl:unless (cl:typep m 'DNSCommand)
    (roslisp-msg-protocol:msg-deprecation-warning "using old message class name nxt_lejos_msgs-msg:<DNSCommand> is deprecated: use nxt_lejos_msgs-msg:DNSCommand instead.")))

(cl:ensure-generic-function 'type-val :lambda-list '(m))
(cl:defmethod type-val ((m <DNSCommand>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader nxt_lejos_msgs-msg:type-val is deprecated.  Use nxt_lejos_msgs-msg:type instead.")
  (type m))

(cl:ensure-generic-function 'value-val :lambda-list '(m))
(cl:defmethod value-val ((m <DNSCommand>))
  (roslisp-msg-protocol:msg-deprecation-warning "Using old-style slot reader nxt_lejos_msgs-msg:value-val is deprecated.  Use nxt_lejos_msgs-msg:value instead.")
  (value m))
(cl:defmethod roslisp-msg-protocol:serialize ((msg <DNSCommand>) ostream)
  "Serializes a message object of type '<DNSCommand>"
  (cl:let ((__ros_str_len (cl:length (cl:slot-value msg 'type))))
    (cl:write-byte (cl:ldb (cl:byte 8 0) __ros_str_len) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 8) __ros_str_len) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 16) __ros_str_len) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 24) __ros_str_len) ostream))
  (cl:map cl:nil #'(cl:lambda (c) (cl:write-byte (cl:char-code c) ostream)) (cl:slot-value msg 'type))
  (cl:let ((bits (roslisp-utils:encode-double-float-bits (cl:slot-value msg 'value))))
    (cl:write-byte (cl:ldb (cl:byte 8 0) bits) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 8) bits) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 16) bits) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 24) bits) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 32) bits) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 40) bits) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 48) bits) ostream)
    (cl:write-byte (cl:ldb (cl:byte 8 56) bits) ostream))
)
(cl:defmethod roslisp-msg-protocol:deserialize ((msg <DNSCommand>) istream)
  "Deserializes a message object of type '<DNSCommand>"
    (cl:let ((__ros_str_len 0))
      (cl:setf (cl:ldb (cl:byte 8 0) __ros_str_len) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 8) __ros_str_len) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 16) __ros_str_len) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 24) __ros_str_len) (cl:read-byte istream))
      (cl:setf (cl:slot-value msg 'type) (cl:make-string __ros_str_len))
      (cl:dotimes (__ros_str_idx __ros_str_len msg)
        (cl:setf (cl:char (cl:slot-value msg 'type) __ros_str_idx) (cl:code-char (cl:read-byte istream)))))
    (cl:let ((bits 0))
      (cl:setf (cl:ldb (cl:byte 8 0) bits) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 8) bits) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 16) bits) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 24) bits) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 32) bits) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 40) bits) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 48) bits) (cl:read-byte istream))
      (cl:setf (cl:ldb (cl:byte 8 56) bits) (cl:read-byte istream))
    (cl:setf (cl:slot-value msg 'value) (roslisp-utils:decode-double-float-bits bits)))
  msg
)
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql '<DNSCommand>)))
  "Returns string type for a message object of type '<DNSCommand>"
  "nxt_lejos_msgs/DNSCommand")
(cl:defmethod roslisp-msg-protocol:ros-datatype ((msg (cl:eql 'DNSCommand)))
  "Returns string type for a message object of type 'DNSCommand"
  "nxt_lejos_msgs/DNSCommand")
(cl:defmethod roslisp-msg-protocol:md5sum ((type (cl:eql '<DNSCommand>)))
  "Returns md5sum for a message object of type '<DNSCommand>"
  "0f40ae65f9de18f2931c26b879f34c47")
(cl:defmethod roslisp-msg-protocol:md5sum ((type (cl:eql 'DNSCommand)))
  "Returns md5sum for a message object of type 'DNSCommand"
  "0f40ae65f9de18f2931c26b879f34c47")
(cl:defmethod roslisp-msg-protocol:message-definition ((type (cl:eql '<DNSCommand>)))
  "Returns full string definition for message of type '<DNSCommand>"
  (cl:format cl:nil "string type~%float64 value~%~%~%~%"))
(cl:defmethod roslisp-msg-protocol:message-definition ((type (cl:eql 'DNSCommand)))
  "Returns full string definition for message of type 'DNSCommand"
  (cl:format cl:nil "string type~%float64 value~%~%~%~%"))
(cl:defmethod roslisp-msg-protocol:serialization-length ((msg <DNSCommand>))
  (cl:+ 0
     4 (cl:length (cl:slot-value msg 'type))
     8
))
(cl:defmethod roslisp-msg-protocol:ros-message-to-list ((msg <DNSCommand>))
  "Converts a ROS message object to a list"
  (cl:list 'DNSCommand
    (cl:cons ':type (type msg))
    (cl:cons ':value (value msg))
))
